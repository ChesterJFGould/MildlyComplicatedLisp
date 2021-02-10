package sexpr;

import java.io.PrintStream;

public class Int extends Sexpr {
	private long val;

	public Int(java.lang.String s) {
		val = Long.parseLong(s);
	}

	public void write(PrintStream ps) {
		ps.print(val);
	}

	public Sexpr eval() {
		return this;
	}
}
