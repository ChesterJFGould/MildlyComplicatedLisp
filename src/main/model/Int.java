package model;

import java.io.PrintStream;

import org.json.*;

// Represents an integer s-expression.
public class Int extends Sexpr {
    private long val;

	// MODIFIES: this
	// EFFECT: Initializes this Int with a value based on the given String.
    public Int(java.lang.String s) {
        val = Long.parseLong(s);
    }

	// MODIFIES: this
	// EFFECT: Initializes this Int with the given value.
    public Int(long val) {
        this.val = val;
    }

    // EFFECT: Returns the string representation of this Int.
    public java.lang.String toString() {
        return Long.toString(val);
    }

    // EFFECT: Return the evaluated form of this Int; Ints are self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECT: Return the Int Type.
    public Type type() {
        return Type.Int;
    }

    // EFFECT: Returns true if expr is an Int and contains the same val, false otherwise.
    public boolean equals(Sexpr expr) {
        if (expr.type() == Type.Int) {
            return ((Int) expr).val == this.val;
        } else {
            return false;
        }
    }

    // EFFECT: Return the val this Int contains.
    public long getVal() {
        return this.val;
    }

	// EFFECT: Returns the JSON representation of this Int.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "integer")
                .put("value", val);
    }
	// EFFECT: Creates and returns a new Int based on the given JSON object.
	// Throws an Exception if the given JSON object doesn't represent an Int.
    public static Int fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("integer") && obj.has("value")) {
            return new Int(obj.getInt("value"));
        } else {
            throw new Exception("cannot parse Int from %s", obj);
        }
    }
}
