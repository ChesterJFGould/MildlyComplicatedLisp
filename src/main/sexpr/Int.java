package sexpr;

import java.io.PrintStream;

public class Int extends Sexpr {
	private long val;

	public Int(java.lang.String s) {
		val = Long.parseLong(s);
	}

	public Int(long val) {
		this.val = val;
	}

	public void write(PrintStream ps) {
		ps.print(this.toString());
	}

	public java.lang.String toString() {
		return Long.toString(val);
	}

	public Sexpr eval(Environment env) {
		return this;
	}

	public Type type() {
		return Type.Int;
	}

	public Sexpr add(Int i) {
		return new Int(this.val + i.val);
	}
}
