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

    public static void initBools(Environment env) {
        env.put("true", new Bool(true));
        env.put("false", new Bool(false));
    }

    public static void initNumericOperators(Environment env) throws sexpr.Exception {
        env.put("+", Procedure.newNumericBinaryOperator("+", (Long a, Long b) -> new Int(a + b),
                (Double a, Double b) -> new sexpr.Float(a + b)));
        env.put("-", Procedure.newNumericBinaryOperator("-", (Long a, Long b) -> new Int(a - b),
                (Double a, Double b) -> new sexpr.Float(a - b)));
        env.put("*", Procedure.newNumericBinaryOperator("*", (Long a, Long b) -> new Int(a * b),
                (Double a, Double b) -> new sexpr.Float(a * b)));
        env.put("/", Procedure.newNumericBinaryOperator("/", (Long a, Long b) -> new Int(a / b),
                (Double a, Double b) -> new sexpr.Float(a / b)));
    }

    public static void initNumericPredicates(Environment env) throws sexpr.Exception {
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
    }

    public static void initPairOperators(Environment env) throws sexpr.Exception {
        env.put("cons", Procedure.newBinaryOperator((Sexpr a, Sexpr b) -> {
            Pair pair = new Pair();
            pair.setCar(a);
            pair.setCdr(b);

            return pair;
        }));
        env.put("car", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCar()));
        env.put("cdr", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCdr()));
    }

    public static void initTypePredicates(Environment env) throws sexpr.Exception {
        env.put("float?", Procedure.newTypePredicate(Type.Int));
        env.put("int?", Procedure.newTypePredicate(Type.Int));
        env.put("null?", Procedure.newTypePredicate(Type.Null));
        env.put("pair?", Procedure.newTypePredicate(Type.Pair));
        env.put("string?", Procedure.newTypePredicate(Type.String));
        env.put("symbol?", Procedure.newTypePredicate(Type.Symbol));
        env.put("bool?", Procedure.newTypePredicate(Type.Bool));
        env.put("procedure?", Procedure.newTypePredicate(Type.Procedure));
    }

    public static void initDefForm(Environment env) throws sexpr.Exception {
        env.put("def", new Procedure("var val", (Environment defEnv, Sexpr defArgs) -> {
            Sexpr var = ((Pair) defArgs).getCar();
            if (var.type() != Type.Symbol) {
                throw new sexpr.Exception("Invalid args to def form %s, variable must be a symbol", defArgs.toString());
            }
            defArgs = ((Pair) defArgs).getCdr();
            Sexpr val = ((Pair) defArgs).getCar().eval(env);

            defEnv.put(((Symbol) var).getVal(), val);

            return new Null();
        }));
    }

    public static void initSetForm(Environment env) throws sexpr.Exception {
        env.put("set!", new Procedure("var val", (Environment defEnv, Sexpr defArgs) -> {
            Sexpr var = ((Pair) defArgs).getCar();
            if (var.type() != Type.Symbol) {
                throw new sexpr.Exception("Invalid args to set! form %s, variable must be a symbol",
                        defArgs.toString());
            }
            defArgs = ((Pair) defArgs).getCdr();
            Sexpr val = ((Pair) defArgs).getCar().eval(env);

            if (!defEnv.set(((Symbol) var).getVal(), val)) {
                throw new sexpr.Exception("Undefined variable %s", var.toString());
            }

            return new Null();
        }));
    }

    public static void initSimpleEquality(Environment env) throws sexpr.Exception {
        env.put("eq?", Procedure.newBinaryOperator((Sexpr a, Sexpr b) -> new Bool(a.equals(b))));
    }

    public static void initPairSetOperators(Environment env) throws sexpr.Exception {
        env.put("set-car!", Procedure.newPairSetter("set-car!", (Pair pair, Sexpr val) -> {
            pair.setCar(val);
            return new Null();
        }));
        env.put("set-cdr!", Procedure.newPairSetter("set-cdr!", (Pair pair, Sexpr val) -> {
            pair.setCdr(val);
            return new Null();
        }));
    }

    public static void initIfForm(Environment env) throws sexpr.Exception {
        env.put("if", new Procedure("predicate consequence alternative", (Environment ifEnv, Sexpr ifArgs) -> {
            Sexpr pred = ((Pair) ifArgs).getCar().eval(ifEnv);
            ifArgs = ((Pair) ifArgs).getCdr();
            if ((pred.type() == Type.Bool) && !((Bool) pred).getVal()) {
                ifArgs = ((Pair) ifArgs).getCdr();
                return ((Pair) ifArgs).getCar().eval(ifEnv);
            } else {
                return ((Pair) ifArgs).getCar().eval(ifEnv);
            }
        }));
    }

    public static void initLambdaForm(Environment env) throws sexpr.Exception {
        env.put("lambda", new Procedure("arguments body", (Environment lamEnv, Sexpr lamArgs) -> {
            Sexpr funVars = ((Pair) lamArgs).getCar();
            lamArgs = ((Pair) lamArgs).getCdr();
            Sexpr body = ((Pair) lamArgs).getCar();
            return new Procedure(funVars, Procedure.evalWrapper((Environment ignored, Sexpr funArgs) -> {
                Environment funEnv = new Environment(lamEnv);
                Sexpr arguments = funVars;

                while (arguments.type() != Type.Null) {
                    if (arguments instanceof Symbol) {
                        funEnv.put(((Symbol) arguments).getVal(), funArgs);
                        break;
                    } else {
                        funEnv.put(((Symbol) ((Pair) arguments).getCar()).getVal(),
                                ((Pair) funArgs).getCar());
                        funArgs = ((Pair) funArgs).getCdr();
                        arguments = ((Pair) arguments).getCdr();
                    }
                }

                return body.eval(funEnv);
            }));
        }));
    }

    public static void initQuoteForm(Environment env) throws sexpr.Exception {
        env.put("quote", new Procedure("obj", (Environment ignored, Sexpr args) -> {
            return ((Pair) args).getCar();
        }));
    }

    public static void initBeginForm(Environment env) throws sexpr.Exception {
        env.put("begin", new Procedure(". exprs", (Environment beginEnv, Sexpr args) -> {
            beginEnv = new Environment(env);

            Sexpr ret = new Null();

            while (args.type() != Type.Null) {
                ret = ((Pair) args).getCar().eval(beginEnv);
                args = ((Pair) args).getCdr();
            }

            return ret;
        }));
    }

    // EFFECTS: Adds the base Simple Lisp constructs to env.
    public static void initSimpleLisp(Environment env) throws sexpr.Exception {
        initBools(env);
        initNumericOperators(env);
        initNumericPredicates(env);
        initPairOperators(env);
        initTypePredicates(env);
        initSimpleEquality(env);
        initDefForm(env);
        initSetForm(env);
        initPairSetOperators(env);
        initIfForm(env);
        initLambdaForm(env);
        initQuoteForm(env);
        initBeginForm(env);
    }
}
