package sexpr;

import java.io.PrintStream;

public class Null extends Sexpr {
	private static Null instance;

	private Null() {}

	public static Null getInstance() {
		if (Null.instance == null) {
			Null.instance = new Null();
		}

		return Null.instance;
	}

	public void write(PrintStream ps) {
		ps.print("()");
	}

	public Sexpr eval() {
		return this;
	}
}