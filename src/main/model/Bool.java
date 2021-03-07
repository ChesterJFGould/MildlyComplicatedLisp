package model;

import java.io.PrintStream;

import org.json.*;

// Represents a boolean s-expession
public class Bool extends Sexpr {
    private boolean val;

    public Bool(boolean val) {
        this.val = val;
    }

    // EFFECT: Returns the string representation of this Bool.
    public java.lang.String toString() {
        if (val) {
            return "true";
        } else {
            return "false";
        }
    }

    // EFFECTS: Returns this, bools are self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECTS: Returns the Bool Type.
    public Type type() {
        return Type.Bool;
    }

    // EFFECTS: Returns the value this Bool contains.
    public boolean getVal() {
        return this.val;
    }

    // EFFECTS: Returns true if expr is a bool and contains the same val, false otherwise.
    public boolean equals(Sexpr expr) {
        if (expr.type() == Type.Bool) {
            return ((Bool) expr).val == this.val;
        } else {
            return false;
        }
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "boolean")
                .put("value", val);
    }

    public static Bool fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("boolean") && obj.has("value")) {
            return new Bool(obj.getBoolean("value"));
        } else {
            throw new Exception("cannot parse Bool from %s", obj);
        }
    }
}
