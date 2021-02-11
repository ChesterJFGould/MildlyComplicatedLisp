package sexpr;

import ltreader.CharStream;
import java.io.PrintStream;

public abstract class Sexpr {
	public abstract void write(PrintStream ps);

	public abstract Sexpr eval(Environment env) throws Exception;

	public abstract java.lang.String toString();

	public abstract Type type();

	public static Sexpr read(CharStream cs) throws Exception {
		cs.eatWhitespace();
		switch (cs.peek()) {
		case '(': //)
			return Sexpr.readList(cs);
		case '"':
			return Sexpr.readString(cs);
		case ')':
			cs.next();
			throw new Exception("Unexpected )");
		default:
			return Sexpr.readAtom(cs);
		}
	}

	private static Sexpr readAtom(CharStream cs) throws Exception {
		java.lang.String atom = cs.readUntil(c -> Character.isWhitespace(c) || "()\"".contains(Character.toString(c)));
		if (atom.matches("[\\-\\+]?\\d+")) {
			return new Int(atom);
		} else if (atom.matches("[\\-\\+]?\\d+\\.\\d*")) {
			return new Float(atom);
		} else {
			return new Symbol(atom);
		}
	}

	private static Sexpr readString(CharStream cs) throws Exception {
		cs.expect('"');

		java.lang.String acc = "";

		while (!cs.done()) {
			acc += cs.readUntil(c -> "\"\\".contains(Character.toString(c)));

			if (cs.peek() == '\\') {
				cs.next();
				acc += "\\" + Character.toString(cs.next());
			} else if (cs.peek() == '"') {
				break;
			} else {
				acc += cs.next();
			}
		}

		cs.expect('"');

		return new String(acc);
	}

	private static Sexpr readList(CharStream cs) throws Exception {
		cs.expect('('); //)

		cs.eatWhitespace();

		if (cs.peek() == ')') {
			cs.next();
			return Null.getInstance();
		}

		Pair head = new Pair();
		Pair pair = head;

		pair.setCar(Sexpr.read(cs));
		cs.eatWhitespace();

		while (/*(*/!".)".contains(cs.peeks())) {
			pair.setCdr(new Pair());
			pair = (Pair)(pair.getCdr());
			pair.setCar(Sexpr.read(cs));
			cs.eatWhitespace();
		}

		if (cs.peek() == '.') {
			cs.next();
			pair.setCdr(Sexpr.read(cs));
		} else {
			pair.setCdr(Null.getInstance());
		}

		cs.expect(')');

		return head;
	}
}
