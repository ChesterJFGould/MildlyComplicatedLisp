package sexpr;

import java.io.PrintStream;

public class Float extends Sexpr {
	private double val;

	public Float(java.lang.String s) {
		this.val = Double.parseDouble(s);
	}

	public void write(PrintStream ps) {
		ps.print(val);
	}

	public Sexpr eval(Environment env) {
		return this;
	}

	public java.lang.String toString() {
		return Double.toString(this.val);
	}

	public Type type() {
		return Type.Float;
	}
}
