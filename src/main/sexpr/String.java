package sexpr;

import java.io.PrintStream;

public class String extends Sexpr {
	private java.lang.String val;

	public String(java.lang.String s) {
		this.val = s;
	}

	public void write(PrintStream ps) {
		ps.print("\"");
		ps.print(val);
		ps.print("\"");
	}

	public Sexpr eval() {
		return this;
	}
}
