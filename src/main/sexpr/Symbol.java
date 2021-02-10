package sexpr;

import java.io.PrintStream;

public class Symbol extends Sexpr {
	private java.lang.String val;

	public Symbol(java.lang.String s) {
		this.val = s.intern();
	}

	public void write(PrintStream ps) {
		ps.print(val);
	}

	public Sexpr eval() {
		return this;
	}
}
