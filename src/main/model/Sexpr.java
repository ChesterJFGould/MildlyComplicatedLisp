package model;

import java.io.PrintStream;

import org.json.*;

// The parent class of all s-expressions.
// Also contains the code to read a new s-expression from a CharStream because
// it seemed appropriate.
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

    public abstract JSONObject toJson();

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
    private static Sexpr readAtom(CharStream cs) {
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

        Pair head = new Pair(Sexpr.read(cs), new Null());
        Pair pair = head;
        cs.eatWhitespace();

        while (/*(*/!".)".contains(cs.peeks()) && !cs.done()) {
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

    // EFFECT: Returns the Sexpr represented by the given JSON object in the
    // given Environment. Throws an Exception if the given JSON object doesn't
    // represent an Sexpr.
    public static Sexpr fromJson(Environment env, JSONObject obj) throws Exception {
        if (!obj.has("type")) {
            throw new Exception("cannot parse Sexpr from %s", obj);
        }

        switch (obj.getString("type")) {
            case "boolean": return Bool.fromJson(obj);
            case "float": return Float.fromJson(obj);
            case "integer": return Int.fromJson(obj);
            case "lambda": return Lambda.fromJson(env, obj);
            case "macro": return Macro.fromJson(env, obj);
            case "null": return Null.fromJson(obj);
            case "pair": return Pair.fromJson(env, obj);
            case "procedure": return Procedure.fromJson(obj);
            case "string": return String.fromJson(obj);
            case "symbol": return Symbol.fromJson(obj);
            case "sound": return Sound.fromJson(obj);
        }

        throw new Exception("cannot parse Sexpr from %s", obj);
    }
}
