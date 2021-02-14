package model;

import java.io.PrintStream;

// Represents a cons cell s-expression, the basic (and only) way of creating
// compound data.
public class Pair extends Sexpr {
    private Sexpr car;
    private Sexpr cdr;

    public Pair() {
    }

    public Pair(Sexpr car, Sexpr cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static Sexpr list(Sexpr... exprs) {
        Sexpr head = new Null();

        for (int i = exprs.length - 1; i >= 0; i--) {
            head = new Pair(exprs[i], head);
        }

        return head;
    }

    // EFFECT: Returns the Sexpr contained in the car.
    public Sexpr getCar() {
        return this.car;
    }

    // MODIFIES: this
    // EFFECT: Sets the car of this Pair to car.
    public void setCar(Sexpr car) {
        this.car = car;
    }

    // EFFECT: Returns the Sexpr contained in the cdr.
    public Sexpr getCdr() {
        return this.cdr;
    }

    // EFFECT: Sets the cdr of this Pair to cdr.
    public void setCdr(Sexpr cdr) {
        this.cdr = cdr;
    }

    // EFFECT: Returns the string representation of this Pair.
    public java.lang.String toString() {
        java.lang.String acc = "(";

        Pair current = this;

        // Prints lists nicely, e.g. (cons 1 (cons 2 '())) gets printed as (1 2)
        while (true) {
            acc += current.car.toString();
            if (current.cdr instanceof Pair) {
                acc += " ";
                current = (Pair) current.cdr;
            } else if (current.cdr.type() == Type.Null) {
                acc += ")";
                break;
            } else {
                acc += " . ";
                acc += current.cdr.toString();
                acc += ")";
                break;
            }
        }

        return acc;
    }

    // EFFECT: Returns the Sexpr this pair evaluates to.
    // If the car is a procedure apply it to the cdr, else throw an exception.
    public Sexpr eval(Environment env) throws Exception {
        Sexpr proc = this.car.eval(env);
        if (!(proc instanceof Procedure)) {
            throw new Exception("Cannot apply non-procedure %s", this.car.toString());
        } else {
            return ((Procedure) proc).apply(env, this.cdr);
        }
    }

    // EFFECT: Returns the Pair Type.
    public Type type() {
        return Type.Pair;
    }

    // EFFECT: Returns true if expr is the same object as this.
    public boolean equals(Sexpr expr) {
        return expr == this;
    }
}