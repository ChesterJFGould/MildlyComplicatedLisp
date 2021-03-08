package model;

import java.io.PrintStream;

import org.json.*;

// Represents a symbol s-expression.
public class Symbol extends Sexpr {
    private java.lang.String val;

	// MODIFIES: this
	// EFFECT: Initializes this Symbol with the given value.
    public Symbol(java.lang.String s) {
        this.val = s.intern(); // String equality becomes pointer equality.
    }

    // EFFECT: Return the string representation of this Symbol.
    public java.lang.String toString() {
        return this.val;
    }

    // EFFECT: Return the Sexpr contained in env associated with val.
    public Sexpr eval(Environment env) throws Exception {
        Sexpr val = env.get(this.val);
        if (val == null) {
            throw new Exception("Undefined variable %s", this.val);
        } else {
            return val;
        }
    }

    // EFFECT: Return the Symbol Type.
    public Type type() {
        return Type.Symbol;
    }

    // EFFECT: Return the value contained in this Symbol.
    public java.lang.String getVal() {
        return this.val;
    }

    // EFFECT: Return true if expr is a Symbol and contains the same val.
    public boolean equals(Sexpr expr) {
        if (expr.type() == Type.Symbol) {
            return ((Symbol) expr).val == this.val; // val is interned.
        } else {
            return false;
        }
    }

	// EFFECT: Returns the JSON representation of this Symbol.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "symbol")
                .put("value", this.val);
    }

	// EFFECT: Creates and returns a new Symbol based on the given JSON object.
	// Throws an Exception if the given JSON object doesn't represent a Symbol.
    public static Symbol fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("symbol") && obj.has("value")) {
            return new Symbol(obj.getString("value"));
        } else {
            throw new Exception("cannot parse Symbol from %s", obj);
        }
    }
}
