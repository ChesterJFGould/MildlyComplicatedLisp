package sexpr;

import java.io.PrintStream;

public class Symbol extends Sexpr {
	private java.lang.String val;

	public Symbol(java.lang.String s) {
		this.val = s.intern();
	}

	public void write(PrintStream ps) {
		ps.print(this.val);
	}

	public java.lang.String toString() {
		return this.val;
	}

	public Sexpr eval(Environment env) throws Exception {
		Sexpr val = env.get(this.val);
		if (val == null) {
			throw new Exception("Undefined variable %s", this.val);
		} else {
			return val;
		}
	}
}
