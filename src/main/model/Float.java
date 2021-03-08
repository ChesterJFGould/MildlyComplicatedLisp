package model;

import java.io.PrintStream;

import org.json.*;

// Represents a floating point number s-expression.
public class Float extends Sexpr {
    private double val;

	// MODIFIES: this
	// EFFECT: Initializes this Float with a value based on the given String.
    public Float(java.lang.String s) {
        this.val = Double.parseDouble(s);
    }

	// MODIFIES: this
	// EFFECT: Initializes this Float with the given value.
    public Float(double d) {
        this.val = d;
    }

    // EFFECT: Returns the string representation of this Float.
    public java.lang.String toString() {
        return Double.toString(this.val);
    }

    // EFFECT: Returns the evaluated form of this Float; Floats are self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECT: Returns the Float Type.
    public Type type() {
        return Type.Float;
    }

    // EFFECT: Returns the val contained in this Float.
    public double getVal() {
        return this.val;
    }

    // EFFECT: Returns true if expr is a Float and contains the same value, false otherwise.
    public boolean equals(Sexpr expr) {
        if (expr.type() == Type.Float) {
            return ((Float) expr).val == this.val;
        } else {
            return false;
        }
    }

	// EFFECT: Returns the JSON representation of this Float.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "float")
                .put("value", this.val);
    }

	// EFFECT: Creates and returns a Float based on the given JSON object.
	// Throws an exception if the given JSON object doesn't represent a Float.
    public static Float fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("float") && obj.has("value")) {
            return new Float(obj.getDouble("value"));
        } else {
            throw new Exception("cannot parse Float from %s", obj);
        }
    }
}
