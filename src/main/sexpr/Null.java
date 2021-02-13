package sexpr;

import java.io.PrintStream;

public class Null extends Sexpr {
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
}
