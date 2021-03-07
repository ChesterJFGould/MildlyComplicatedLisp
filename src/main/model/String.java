package model;

import java.io.PrintStream;

import org.json.*;

// Represents a string s-expression.
public class String extends Sexpr {
    private java.lang.String val;

    public String(java.lang.String s) {
        this.val = s;
    }

    // EFFECT: Returns the string representation of this String.
    public java.lang.String toString() {
        return java.lang.String.format("\"%s\"", this.val);
    }

    // EFFECT: Returns the evaluated form of this String. Strings are
    // self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECT: Returns the Type String.
    public Type type() {
        return Type.String;
    }

    // EFFECT: Returns true if expr is a String and expr contains the same val as
    // this.
    public boolean equals(Sexpr expr) {
        if (expr.type() == Type.String) {
            return ((String) expr).val.equals(this.val);
        } else {
            return false;
        }
    }

    // EFFECT: Returns the val stored in this String.
    public java.lang.String getVal() {
        return this.val;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "string")
                .put("value", this.val);
    }

    public static String fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("string") && obj.has("value")) {
            return new String(obj.getString("value"));
        } else {
            throw new Exception("cannot parse String from %s", obj);
        }
    }
}
