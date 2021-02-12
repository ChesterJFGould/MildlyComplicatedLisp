package ui;

import sexpr.Sexpr;
import ltreader.CharStream;
import sexpr.*;

public class Main {
	// EFFECTS: Sets up the Simple Lisp environment and starts the repl.
	public static void main(java.lang.String[] args) throws sexpr.Exception {
		sexpr.Environment env = new sexpr.Environment();

		initSimpleLisp(env);

		repl(env);
	}

	// EFFECTS: Starts a repl (Read, Eval, Print, Loop).
	public static void repl(sexpr.Environment env) {
		System.out.print("> ");

		CharStream stdin = new CharStream(System.in);
		while (!stdin.done()) {
			try {
				Sexpr expr = Sexpr.read(stdin);
				expr.eval(env).write(System.out);
				System.out.println("");
			} catch (sexpr.Exception e) {
				System.out.println("Error : " + e.getMessage());
			}

			System.out.print("> ");
		}
		System.out.println("");
	}

	// EFFECTS: Adds the base Simple Lisp constructs to env.
	public static void initSimpleLisp(Environment env) throws sexpr.Exception {
		// Bools
		env.put("true", new Bool(true));
		env.put("false", new Bool(false));

		// Numeric operators.
		env.put("+", Procedure.newNumericBinaryOperator("+", (Long a, Long b) -> new Int(a + b),
			(Double a, Double b) -> new sexpr.Float(a + b)));
		env.put("-", Procedure.newNumericBinaryOperator("-", (Long a, Long b) -> new Int(a - b),
			(Double a, Double b) -> new sexpr.Float(a - b)));
		env.put("*", Procedure.newNumericBinaryOperator("*", (Long a, Long b) -> new Int(a * b),
			(Double a, Double b) -> new sexpr.Float(a * b)));
		env.put("/", Procedure.newNumericBinaryOperator("/", (Long a, Long b) -> new Int(a / b),
			(Double a, Double b) -> new sexpr.Float(a / b)));
		// Numeric predicates.
		env.put("<", Procedure.newNumericBinaryOperator("<", (Long a, Long b) -> new Bool(a < b),
			(Double a, Double b) -> new Bool(a < b)));
		env.put(">", Procedure.newNumericBinaryOperator(">", (Long a, Long b) -> new Bool(a > b),
			(Double a, Double b) -> new Bool(a > b)));
		env.put("=", Procedure.newNumericBinaryOperator("=", (Long a, Long b) -> new Bool(a == b),
			(Double a, Double b) -> new Bool(a == b)));
		env.put("<=", Procedure.newNumericBinaryOperator("<=", (Long a, Long b) -> new Bool(a <= b),
			(Double a, Double b) -> new Bool(a <= b)));
		env.put(">=", Procedure.newNumericBinaryOperator(">=", (Long a, Long b) -> new Bool(a >= b),
			(Double a, Double b) -> new Bool(a >= b)));

		// Pair operators.
		env.put("cons", Procedure.newBinaryOperator((Sexpr a, Sexpr b) -> {
			Pair pair = new Pair();
			pair.setCar(a);
			pair.setCdr(b);

			return pair;
		}));
		env.put("car", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCar()));
		env.put("cdr", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCdr()));

		// Type predicates.
		env.put("float?", Procedure.newTypePredicate(Type.Int));
		env.put("int?", Procedure.newTypePredicate(Type.Int));
		env.put("null?", Procedure.newTypePredicate(Type.Null));
		env.put("pair?", Procedure.newTypePredicate(Type.Pair));
		env.put("string?", Procedure.newTypePredicate(Type.String));
		env.put("symbol?", Procedure.newTypePredicate(Type.Symbol));
		env.put("bool?", Procedure.newTypePredicate(Type.Bool));
		env.put("procedure?", Procedure.newTypePredicate(Type.Procedure));

		// Simple equality predicate.
		env.put("eq?", Procedure.newBinaryOperator((Sexpr a, Sexpr b) -> new Bool(a.equals(b))));

		// Definition form.
		env.put("def", new Procedure("var val", (Environment defEnv, Sexpr defArgs) -> {
			Sexpr var = ((Pair)defArgs).getCar();
			if (var.type() != Type.Symbol) {
				throw new sexpr.Exception("Invalid args to def form %s, variable must be a symbol", defArgs.toString());
			}
			defArgs = ((Pair)defArgs).getCdr();
			Sexpr val = ((Pair)defArgs).getCar().eval(env);

			defEnv.put(((Symbol)var).getVal(), val);

			return new Null();
		}));

		// Set form.
		env.put("set!", new Procedure("var val", (Environment defEnv, Sexpr defArgs) -> {
			Sexpr var = ((Pair)defArgs).getCar();
			if (var.type() != Type.Symbol) {
				throw new sexpr.Exception("Invalid args to set! form %s, variable must be a symbol", defArgs.toString());
			}
			defArgs = ((Pair)defArgs).getCdr();
			Sexpr val = ((Pair)defArgs).getCar().eval(env);

			if (!defEnv.set(((Symbol)var).getVal(), val)) {
				throw new sexpr.Exception("Undefined variable %s", var.toString());
			}

			return new Null();
		}));

		// Pair set forms.
		env.put("set-car!", Procedure.newPairSetter("set-car!", (Pair pair, Sexpr val) -> {
				pair.setCar(val);
				return new Null();
			}));
		env.put("set-cdr!", Procedure.newPairSetter("set-cdr!", (Pair pair, Sexpr val) -> {
				pair.setCdr(val);
				return new Null();
			}));

		// If form.
		env.put("if", new Procedure("predicate consequence alternative", (Environment ifEnv, Sexpr ifArgs) -> {
			Sexpr pred = ((Pair)ifArgs).getCar().eval(ifEnv);
			ifArgs = ((Pair)ifArgs).getCdr();
			if ((pred.type() == Type.Bool) && !((Bool)pred).getVal()) {
				ifArgs = ((Pair)ifArgs).getCdr();
				return ((Pair)ifArgs).getCar().eval(ifEnv);
			} else {
				return ((Pair)ifArgs).getCar().eval(ifEnv);
			}
		}));

		// Lambda form.
		env.put("lambda", new Procedure("arguments body", (Environment lamEnv, Sexpr lamArgs) -> {
			Sexpr funVars = ((Pair)lamArgs).getCar();
			lamArgs = ((Pair)lamArgs).getCdr();
			Sexpr body = ((Pair)lamArgs).getCar();
			return new Procedure(funVars, Procedure.evalWrapper((Environment ignored, Sexpr funArgs) -> {
				Environment funEnv = new Environment(lamEnv);
				Sexpr arguments = funVars;

				while (arguments.type() != Type.Null) {
					if (arguments instanceof Symbol) {
						funEnv.put(((Symbol)arguments).getVal(), funArgs);
						break;
					} else {
						funEnv.put(((Symbol)((Pair)arguments).getCar()).getVal(),
							((Pair)funArgs).getCar());
						funArgs = ((Pair)funArgs).getCdr();
						arguments = ((Pair)arguments).getCdr();
					}
				}

				return body.eval(funEnv);
			}));
		}));

		// Quote form.
		env.put("quote", new Procedure("obj", (Environment ignored, Sexpr args) -> {
			return ((Pair)args).getCar();
		}));
	}
}
