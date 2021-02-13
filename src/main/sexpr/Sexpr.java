package sexpr;

import ltreader.CharStream;

import java.io.PrintStream;

public abstract class Sexpr {
    // Write a representation of the Sexpr to ps.
    public void write(PrintStream ps) {
        ps.print(this.toString());
    }

    // Return the evaluated form of the Sexpr w.r.t. env.
    public abstract Sexpr eval(Environment env) throws Exception;

    // Return a string representation of the Sexpr.
    public abstract java.lang.String toString();

    // Return the Type of the Sexpr.
    public abstract Type type();

    // Return true if expr is equal to this. The definition of equality may vary
    // depending of the kind of expr.
    public abstract boolean equals(Sexpr expr);

    // EFFECT: Return an s-expression read from cs. Throws an exception if something goes wrong
    // during parsing.
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
            case '\'':
                cs.next();
                return new Pair(new Symbol("quote"), new Pair(Sexpr.read(cs), new Null()));
            default:
                return Sexpr.readAtom(cs);
        }
    }

    // EFFECT: Return an atom (Symbol, Int, Float) read from cs.
    private static Sexpr readAtom(CharStream cs) throws Exception {
        java.lang.String atom = cs.readUntil(
                c -> Character.isWhitespace(c) || "()\"".contains(Character.toString(c)));
        if (atom.matches("[\\-\\+]?\\d+")) {
            return new Int(atom);
        } else if (atom.matches("[\\-\\+]?\\d+\\.\\d*")) {
            return new Float(atom);
        } else {
            return new Symbol(atom);
        }
    }

    // EFFECT: Return a String read from cs.
    private static Sexpr readString(CharStream cs) throws Exception {
        cs.expect('"');

        java.lang.String acc = "";

        while (!cs.done()) {
            acc += cs.readUntil(c -> "\"\\".contains(Character.toString(c)));

            if (cs.peek() == '\\') {
                cs.next();
                acc += Character.toString(cs.next());
            } else if (cs.peek() == '"') {
                break;
            }
        }

        cs.expect('"');

        return new String(acc);
    }

    // EFFECT: Return a Pair read from cs.
    private static Sexpr readList(CharStream cs) throws Exception {
        cs.expect('('); //)
        cs.eatWhitespace();

        if (cs.peek() == ')') {
            cs.next();
            return new Null();
        }

        Pair head = new Pair();
        Pair pair = head;

        pair.setCar(Sexpr.read(cs));
        cs.eatWhitespace();

        while (/*(*/!".)".contains(cs.peeks())) {
            pair.setCdr(new Pair(Sexpr.read(cs), new Null()));
            pair = (Pair) (pair.getCdr());
            cs.eatWhitespace();
        }

        if (cs.peek() == '.') {
            cs.next();
            pair.setCdr(Sexpr.read(cs));
        }

        cs.expect(')');

        return head;
    }
}
