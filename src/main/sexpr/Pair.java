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
		ps.print("("); //)

		Pair current = this;

		while (true) {
			current.car.write(ps);

			if (current.cdr instanceof Pair) {
				ps.print(" ");
				current = (Pair)current.cdr;
			} else if (current.cdr == Null.getInstance()) {
				ps.print(")");
				break;
			} else {
				ps.print(" . ");
				current.cdr.write(ps);
				ps.print(")");
				break;
			}
		}
	}

	public Sexpr eval() {
		return this;
	}
}
