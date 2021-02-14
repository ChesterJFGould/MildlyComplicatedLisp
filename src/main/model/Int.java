package model;

import java.io.PrintStream;

// Represents an integer s-expression.
public class Int extends Sexpr {
    private long val;

    public Int(java.lang.String s) {
        val = Long.parseLong(s);
    }

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
}