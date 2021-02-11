package sexpr;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Procedure extends Sexpr {
	private Signature signature;
	private Handler handler;

	public Procedure(java.lang.String signature, Handler handler) throws Exception {
		this.signature = new Signature(signature);
		this.handler = handler;
	}

	public Procedure(Sexpr signature, Handler handler) throws Exception {
		this.signature = new Signature(signature);
		this.handler = handler;
	}

	public interface Handler {
		public Sexpr handle(Environment env, Sexpr args) throws Exception;
	}

	// EFFECT: Returns a handler that evals the given arguments before passing them
	// to handler.
	public static Handler evalWrapper(Handler handler) throws Exception {
		return (Environment env, Sexpr args) -> {
			Sexpr evaledArgs = new Null();
			if (args.type() != Type.Null) {
				evaledArgs = new Pair();
				Pair pair = (Pair)evaledArgs;
				pair.setCar(((Pair)args).getCar().eval(env));
				args = ((Pair)args).getCdr();

				while (!(args instanceof Null)) {
					pair.setCdr(new Pair());
					pair = (Pair)pair.getCdr();
					pair.setCar(((Pair)args).getCar().eval(env));
					args = ((Pair)args).getCdr();
				}

				pair.setCdr(new Null());
			}

			return handler.handle(env, evaledArgs);
		};
	}

	// EFFECT: Returns a Procedure that takes in two arguments. If the arguments
	// are both of the same numeric type (Int, Float) then pass them to the
	// appropriate handler and return the result. If the arguments are not both of
	// the same numeric type, or are not numeric at all, throw an exception.
	public static Procedure newNumericBinaryOperator(java.lang.String name, BiFunction<Long, Long, Sexpr> intOp,
		BiFunction<Double, Double, Sexpr> floatOp) throws Exception {
		return new Procedure("a b", Procedure.evalWrapper((Environment env, Sexpr args) -> {
			Sexpr a = ((Pair)args).getCar();
			args = ((Pair)args).getCdr();
			Sexpr b = ((Pair)args).getCar();

			switch (a.type()) {
			case Int:
				if (b.type() == Type.Int) {
					return intOp.apply(((Int)a).getVal(), ((Int)b).getVal());
				}
				break;
			case Float:
				if (b.type() == Type.Float) {
					return floatOp.apply(((Float)a).getVal(), ((Float)b).getVal());
				}
				break;
			}

			throw new Exception("Arguments to %s must both be of the same numeric type, (%s %s)", name,
				a.toString(), b.toString());
		}));
	}

	// EFFECT: Returns a Procedure that takes in two arguments. The procedure
	// returns the result of applying op to these two arguments.
	public static Procedure newBinaryOperator(BiFunction<Sexpr, Sexpr, Sexpr> op) throws Exception {
		return new Procedure("a b", Procedure.evalWrapper((Environment env, Sexpr args) -> {
			Sexpr a = ((Pair)args).getCar();
			args = ((Pair)args).getCdr();
			Sexpr b = ((Pair)args).getCar();

			return op.apply(a, b);
		}));
	}

	// EFFECT: Returns a Procedure that takes in an argument. This procedure return
	// true if the argument of type, else false.
	public static Procedure newTypePredicate(Type type) throws Exception {
		return new Procedure("obj", Procedure.evalWrapper((Environment env, Sexpr args) -> {
			Sexpr obj = ((Pair)args).getCar();

			return new Bool(obj.type() == type);
		}));
	}

	// EFFECT: Returns a Procedure that takes in an argument. If the argument is
	// a Pair the Procedure returns the result of applying op to it, otherwise it
	// throws an Exception.
	public static Procedure newPairUnaryOperator(java.lang.String name, Function<Pair, Sexpr> op) throws Exception {
		return new Procedure("pair", Procedure.evalWrapper((Environment env, Sexpr args) -> {
			Sexpr pair = ((Pair)args).getCar();

			if (pair.type() == Type.Pair) {
				return op.apply((Pair)pair);
			} else {
				throw new Exception("Argument to %s must be of type pair", name);
			}
		}));
	}

	// EFFECCT: Returns a Procedure that takes in two arguments. If the first isn't
	// a pair then throw an exception, else return the result of applying op to the
	// arguments. Meant to be used to implements set-car/cdr, hence the name.
	public static Procedure newPairSetter(java.lang.String name, BiFunction<Pair, Sexpr, Sexpr> op) throws Exception {
		return new Procedure("pair val", Procedure.evalWrapper((Environment env, Sexpr args) -> {
			Sexpr pair = ((Pair)args).getCar();
			if (pair.type() != Type.Pair) {
				throw new Exception("Invalid arg to %s, %s must be a pair", name, pair.toString());
			}

			args = ((Pair)args).getCdr();
			Sexpr val = ((Pair)args).getCar();

			return op.apply((Pair)pair, val);
		}));
	}

	public Sexpr eval(Environment env) {
		return this;
	}

	public Type type() {
		return Type.Procedure;
	}

	public Sexpr apply(Environment env, Sexpr args) throws Exception {
		if (signature.validate(args)) {
			return this.handler.handle(env, args);
		} else {
			throw new Exception("Cannot apply args %s to procedure with signature %s",
				args.toString(),
				this.signature.toString());
		}
	}

	public void write(PrintStream ps) {
		ps.print(this.toString());
	}

	public java.lang.String toString() {
		return java.lang.String.format("<Procedure %s>", this.signature.toString());
	}

	public boolean equals(Sexpr expr) {
		return expr == this;
	}

	private class Signature {
		private List<java.lang.String> args;
		private java.lang.String vararg;

		public Signature(Sexpr args) throws Exception {
			this.args = new ArrayList<java.lang.String>();
			this.vararg = null;

			this.parse(args);
		}

		public Signature(java.lang.String args) throws Exception {
			this.args = new ArrayList<java.lang.String>();
			this.vararg = null;

			this.parse(args);
		}

		public java.lang.String toString() {
			java.lang.String acc = "(" + java.lang.String.join(" ", this.args);
			if (this.vararg != null) {
				acc += " . " + vararg;
			}

			return acc + ")";
		}

		public boolean validate(Sexpr args) {
			Sexpr current = args;
			for (java.lang.String s : this.args) {
				if (!(current instanceof Pair)) {
					return false;
				} else {
					current = ((Pair)current).getCdr();
				}
			}
			if (!(current instanceof Null) && this.vararg == null) {
				return false;
			}

			return true;
		}

		private void parse(Sexpr args) throws Exception {
			if (args instanceof Pair && ((Pair)args).getCar() instanceof Symbol) {
				this.args.add(((Pair)args).getCar().toString());
				this.parse(((Pair)args).getCdr());
			} else if (args instanceof Symbol) {
				this.vararg = ((Symbol)args).toString();
			} else if (args.type() == Type.Null) {

			} else {
				throw new Exception("Invalid Procedure Signature %s", args.toString());
			}
		}

		private void parse(java.lang.String args) throws Exception {
			java.lang.String[] vars = args.split(" ");

			for (int i = 0; i < vars.length; i++) {
				if (vars[i] == ".") {
					if (vars.length != i + 2) {
						throw new Exception("Invalid Procedure Signature %s", args);
					} else {
						this.vararg = vars[i + 1];
					}
				} else {
					this.args.add(vars[i]);
				}
			}
		}

	}

}
