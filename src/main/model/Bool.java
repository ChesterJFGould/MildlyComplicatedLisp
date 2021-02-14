package model;

import java.io.PrintStream;

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
}