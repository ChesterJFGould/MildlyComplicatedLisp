package sexpr;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;

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

	/*
	public static newFunction(Sexpr signature, Handler handler) throws Exception {
		Handler wrapper = (Environment env, Sexpr args) -> {

		}
	}
	*/

	public Sexpr eval(Environment env) {
		return this;
	}

	public

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

	public interface Handler {
		public Sexpr handle(Environment env, Sexpr args) throws Exception;
	}
}
