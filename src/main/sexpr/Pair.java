package sexpr;

import java.io.PrintStream;

public class Pair extends Sexpr {
	private Sexpr car;
	private Sexpr cdr;

	public Pair() {}

	public Sexpr getCar() {
		return this.car;
	}

	public void setCar(Sexpr car) {
		this.car = car;
	}

	public Sexpr getCdr() {
		return this.cdr;
	}

	public void setCdr(Sexpr cdr) {
		this.cdr = cdr;
	}

	public void write(PrintStream ps) {
		ps.print(this.toString());
	}

	public java.lang.String toString() {
		java.lang.String acc = "(";

		Pair current = this;

		while (true) {
			acc += current.car.toString();
			if (current.cdr instanceof Pair) {
				acc += " ";
				current = (Pair)current.cdr;
			} else if (current.cdr == Null.getInstance()) {
				acc += ")";
				break;
			} else {
				acc += " . ";
				acc += current.cdr.toString();
				acc += ")";
				break;
			}
		}

		return acc;
	}

	public Sexpr eval(Environment env) throws Exception {
		Sexpr proc = this.car.eval(env);
		if (!(proc instanceof Procedure)) {
			throw new Exception("Cannot apply non-procedure %s", this.car.toString());
		} else {
			return ((Procedure)proc).apply(env, this.cdr);
		}
	}

	public Type type() {
		return Type.Pair;
	}
}
