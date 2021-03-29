package model;

import org.json.*;

import java.util.HashMap;

// Represents a cons cell s-expression, the basic (and only) way of creating
// compound data.
public class Pair extends Sexpr {
    private Sexpr car;
    private Sexpr cdr;
    private boolean serialized;
    private long ptr;

    private static Heap<Pair> heap = new Heap<>();

    // MODIFIES: heap
    // EFFECT: Replaces heap with a new heap.
    public static void resetHeap() {
        heap = new Heap<>();
    }

    // MODIFIES: heap
    // EFFECT: Sets the heap ptr to be higher than all entries it contains.
    public static void restoreHeapPointer() {
        long max = 0;
        for (HashMap.Entry<Long, Pair> entry : heap.getHeap().entrySet()) {
            if (entry.getValue().ptr > max) {
                max = entry.getValue().ptr;
            }
        }

        heap.setPtr(max + 1);
    }

    // MODIFIES: heap
    // EFFECT: Sets the serialized tags of all the Environments in the heap
    // to false.
    public static void resetSerializedTags() {
        for (HashMap.Entry<Long, Pair> entry : heap.getHeap().entrySet()) {
            entry.getValue().serialized = false;
        }
    }

    // EFFECT: Returns the heap.
    public static Heap<Pair> getHeap() {
        return Pair.heap;
    }

    // EFFECT: Default constructor made incarnate.
    public Pair() {
        this.car = null;
        this.cdr = null;
        this.serialized = false;
        this.ptr = heap.malloc(this);
    }

    // MODIFIES: this
    // EFFECT: Initializes this Pair with the given pointer.
    public Pair(long ptr) {
        this.serialized = false;
        this.ptr = ptr;
        heap.put(ptr, this);
    }

    // MODIFIES: this
    // EFFECT: Initializes this Pair with the given car and cdr.
    public Pair(Sexpr car, Sexpr cdr) {
        this.car = null;
        this.cdr = null;
        this.car = car;
        this.cdr = cdr;
        this.serialized = false;
        this.ptr = heap.malloc(this);
    }

    // EFFECT: Creates and returns a new Pair representing a list of the given exprs.
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
    @Override
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

    // EFFECT: Returns the JSON representation of this Pair.
    public JSONObject toJson() {
        if (this.serialized) {
            return new JSONObject()
                    .put("type", "pair")
                    .put("ptr", this.ptr);
        }

        this.serialized = true;

        return new JSONObject()
                .put("type", "pair")
                .put("ptr", this.ptr)
                .put("car", this.car.toJson())
                .put("cdr", this.cdr.toJson());
    }

    // EFFECT: Creates and returns a new Pair based on the given JSON object.
    // Throws an Exception if the given JSON object doesn't represent a Pair.
    public static Pair fromJson(Environment env, JSONObject obj) throws Exception {
        if (!obj.has("type") || !obj.getString("type").equals("pair") || !obj.has("ptr")) {
            throw new Exception("cannot parse Pair from %s", obj);
        }

        long ptr = obj.getLong("ptr");

        Pair pair = Pair.heap.get(ptr);

        if (pair != null) {
            return pair;
        } else if (!obj.has("car") || !obj.has("cdr")) {
            throw new Exception("cannot parse Pair from %s", obj);
        }

        pair = new Pair(ptr);
        pair.car = Sexpr.fromJson(env, obj.getJSONObject("car"));
        pair.cdr = Sexpr.fromJson(env, obj.getJSONObject("cdr"));

        return pair;
    }
}
