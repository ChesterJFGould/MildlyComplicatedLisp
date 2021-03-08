package model;

import java.io.PrintStream;

import org.json.*;

// Represents the null (a.k.a. empty list, unit) s-expression.
public class Null extends Sexpr {
	// EFFECTS: Default constructor made incarnate.
    public Null() {
    }

    // EFFECT: Returns the evaluated form of Null; Null is self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECT: Returns the string representation of Null.
    public java.lang.String toString() {
        return "()";
    }

    // EFFECT: Returns the Null Type.
    public Type type() {
        return Type.Null;
    }

    // EFFECT: Returns true of expr is also a Null.
    public boolean equals(Sexpr expr) {
        return expr.type() == Type.Null;
    }

	// EFFECT: Returns the JSON representation of this Null.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "null");
    }

	// EFFECT: Creates and returns a new Null based on the given JSON object.
	// Throws an Exception if the given JSON object doesn't represent a Null.
    public static Null fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("null")) {
            return new Null();
        } else {
            throw new Exception("cannot parse Null from %s", obj);
        }
    }
}
