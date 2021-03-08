package model;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.HashMap;

import org.json.*;

// Represents the generalized version of a procedure s-expression.
// Generalized in the sense that arguments are not necessarily evaluated when
// passed to the procedure.
// All special forms, macros, and lambdas are a Procedure.
public class Procedure extends Sexpr {
    protected Signature signature;
    private Handler handler;
    private java.lang.String name;

    private static HashMap<java.lang.String, Procedure> procedures = new HashMap<>();

    // MODIFIES: this
    // EFFECT: Initializes this Procedure with the given String representation of a
    // Signature, and Handler. Adds this to Procedure.procedures under the given name.
    public Procedure(java.lang.String name, java.lang.String signature, Handler handler) throws Exception {
        this.signature = new Signature(signature);
        this.handler = handler;
        this.name = name;

        procedures.put(name, this);
    }

    // MODIFIES: this
    // EFFECT: Initializes this Procedure with the given Sexpr representation of a
    // Signature, and Handler. Adds this to Procedure.procedures under the given name.
    public Procedure(java.lang.String name, Sexpr signature, Handler handler) throws Exception {
        this.signature = new Signature(signature);
        this.handler = handler;
        this.name = name;

        procedures.put(name, this);
    }

    // MODIFIES: this
    // EFFECT: Initializes this Procedure with the given Sexpr representation of a
    // Signature, and Handler.
    public Procedure(Sexpr signature, Handler handler) throws Exception {
        this.signature = new Signature(signature);
        this.handler = handler;
    }

    // MODIFIES: this
    // EFFECT: Initializes this Procedure with the given Signature and Handler.
    public Procedure(Signature signature, Handler handler) {
        this.signature = signature;
        this.handler = handler;
    }

    // Represents a procedure that acts upon an Environment and Sexpr arguments.
    public interface Handler {
        public Sexpr handle(Environment env, Sexpr args) throws Exception;
    }

    // EFFECT: Returns the Procedure stored under the given name in
    // Procedure.procedures or null if no entry exists.
    public static Procedure getProcedure(java.lang.String name) {
        return procedures.get(name);
    }

    // EFFECT: Returns a handler that evals the given arguments before passing them
    // to handler.
    public static Handler evalWrapper(Handler handler) throws Exception {
        return (Environment env, Sexpr args) -> {
            Sexpr evaledArgs = new Null();
            if (args.type() != Type.Null) {
                evaledArgs = new Pair();
                Pair pair = (Pair) evaledArgs;
                pair.setCar(((Pair) args).getCar().eval(env));
                args = ((Pair) args).getCdr();

                while (args.type() != Type.Null) {
                    pair.setCdr(new Pair());
                    pair = (Pair) pair.getCdr();
                    pair.setCar(((Pair) args).getCar().eval(env));
                    args = ((Pair) args).getCdr();
                }

                pair.setCdr(new Null());
            }

            return handler.handle(env, evaledArgs);
        };
    }

    // EFFECT: Returns a Procedure that takes in two arguments. If the arguments
    // are both of the same numeric type (Int, Float) then pass them to the
    // appropriate handler and return the result. If the arguments are not both of
    // the same numeric type, or are not numeric at all, throw an exception.
    public static Procedure newNumericBinaryOperator(java.lang.String name, BiFunction<Long, Long, Sexpr> intOp,
                                                     BiFunction<Double, Double, Sexpr> floatOp) throws Exception {
        return new Procedure(name, "a b", Procedure.evalWrapper((Environment env, Sexpr args) -> {
            Sexpr a = ((Pair) args).getCar();
            args = ((Pair) args).getCdr();
            Sexpr b = ((Pair) args).getCar();

            switch (a.type()) {
                case Int:
                    if (b.type() == Type.Int) {
                        return intOp.apply(((Int) a).getVal(), ((Int) b).getVal());
                    }
                    break;
                case Float:
                    if (b.type() == Type.Float) {
                        return floatOp.apply(((Float) a).getVal(), ((Float) b).getVal());
                    }
                    break;
            }

            throw new Exception("Arguments to %s must both be of the same numeric type, (%s %s)", name,
                    a.toString(), b.toString());
        }));
    }

    // EFFECT: Returns a Procedure that takes in two arguments. The procedure
    // returns the result of applying op to these two arguments.
    public static Procedure newBinaryOperator(java.lang.String name, BiFunction<Sexpr, Sexpr, Sexpr> op)
            throws Exception {
        return new Procedure(name, "a b", Procedure.evalWrapper((Environment env, Sexpr args) -> {
            Sexpr a = ((Pair) args).getCar();
            args = ((Pair) args).getCdr();
            Sexpr b = ((Pair) args).getCar();

            return op.apply(a, b);
        }));
    }

    // EFFECT: Returns a Procedure that takes in an argument. This procedure return
    // true if the argument of type, else false.
    public static Procedure newTypePredicate(java.lang.String name, Type type) throws Exception {
        return new Procedure(name, "obj", Procedure.evalWrapper((Environment env, Sexpr args) -> {
            Sexpr obj = ((Pair) args).getCar();

            return new Bool(obj.type() == type);
        }));
    }

    // EFFECT: Returns a Procedure that takes in an argument. If the argument is
    // a Pair the Procedure returns the result of applying op to it, otherwise it
    // throws an Exception.
    public static Procedure newPairUnaryOperator(java.lang.String name, Function<Pair, Sexpr> op) throws Exception {
        return new Procedure(name, "pair", Procedure.evalWrapper((Environment env, Sexpr args) -> {
            Sexpr pair = ((Pair) args).getCar();

            if (pair.type() == Type.Pair) {
                return op.apply((Pair) pair);
            } else {
                throw new Exception("Argument to %s must be of type pair", name);
            }
        }));
    }

    // EFFECT: Returns a Procedure that takes in two arguments. If the first isn't
    // a pair then throw an exception, else return the result of applying op to the
    // arguments. Meant to be used to implements set-car/cdr, hence the name.
    public static Procedure newPairSetter(java.lang.String name, BiFunction<Pair, Sexpr, Sexpr> op) throws Exception {
        return new Procedure(name, "pair val", Procedure.evalWrapper((Environment env, Sexpr args) -> {
            Sexpr pair = ((Pair) args).getCar();
            if (pair.type() != Type.Pair) {
                throw new Exception("Invalid arg to %s, %s must be a pair", name, pair.toString());
            }

            args = ((Pair) args).getCdr();
            Sexpr val = ((Pair) args).getCar();

            return op.apply((Pair) pair, val);
        }));
    }

    // EFFECT: Returns the evaluated form of this Procedure. Procedures are
    // self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECT: Returns the Type Procedure.
    public Type type() {
        return Type.Procedure;
    }

    // EFFECT: Calls this Procedures handler with the give arguments in the given
    // environment and returns the result.
    public Sexpr apply(Environment env, Sexpr args) throws Exception {
        if (signature.validate(args)) {
            return this.handler.handle(env, args);
        } else {
            throw new Exception("Cannot apply args %s to procedure with signature %s",
                    args.toString(),
                    this.signature.toString());
        }
    }

    // EFFECT: Returns the string representation of this Procedure.
    public java.lang.String toString() {
        return java.lang.String.format("<Procedure %s>", this.signature.toString());
    }

    // EFFECT: Returns true if expr is the same object as this.
    public boolean equals(Sexpr expr) {
        return expr == this;
    }

    // EFFECT: Returns the JSON representation of this Procedure.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "procedure")
                .put("name", this.name);
    }

    // EFFECT: Creates and returns a new Procedure based on the given JSON object.
    // Throws an Exception if the given JSON object doesn't represent a Procedure or
    // the named procedure isn't contained in Procedure.procedures.
    public static Procedure fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("procedure") && obj.has("name")) {
            Procedure proc = Procedure.getProcedure(obj.getString("name"));
            if (proc == null) {
                throw new Exception("cannot parse Procedure from %s, no procedure has name %s", obj,
                        obj.getString("name"));
            } else {
                return proc;
            }
        } else {
            throw new Exception("cannot parse Procedure from %s", obj);
        }
    }

    // Represents a method signature. Used to verify that the given arguments
    // match what the handler wants.
    public static class Signature {
        private List<java.lang.String> args;
        private java.lang.String vararg;

        // MODIFIES: this
        // EFFECT: Initializes this Signature with arguments based on the given Sexpr.
        public Signature(Sexpr args) throws Exception {
            this.args = new ArrayList<java.lang.String>();
            this.vararg = null;

            this.parse(args);
        }

        // MODIFIES: this
        // EFFECT: Initializes this Signature with arguments based on the given String.
        public Signature(java.lang.String args) throws Exception {
            this.args = new ArrayList<java.lang.String>();
            this.vararg = null;

            this.parse(args);
        }

        // MODIFIES: this
        // EFFECT: Initializes this Signature with the given arguments.
        public Signature(List<java.lang.String> args, java.lang.String vararg) {
            this.args = args;
            this.vararg = vararg;
        }

        // EFFECT: Returns the string representation of this Signature.
        public java.lang.String toString() {
            java.lang.String acc = "(" + java.lang.String.join(" ", this.args);
            if (this.vararg != null) {
                acc += " . " + vararg;
            }

            return acc + ")";
        }

        // EFFECT: Returns the Sexpr representation of this Signature.
        public Sexpr toSexpr() {
            Sexpr signature = new Null();
            if (this.vararg != null) {
                signature = new Symbol(this.vararg);
            }

            for (int i = this.args.size() - 1; i >= 0; i--) {
                signature = new Pair(new Symbol(this.args.get(i)), signature);
            }

            return signature;
        }

        // EFFECT: Returns true if args is a list that matches the signature,
        // false otherwise.
        // e.g. "a b" matches (1 2), "a" doesn't match (1 2).
        public boolean validate(Sexpr args) {
            Sexpr current = args;
            for (java.lang.String s : this.args) {
                if (!(current instanceof Pair)) {
                    return false;
                } else {
                    current = ((Pair) current).getCdr();
                }
            }
            if (!(current instanceof Null) && this.vararg == null) {
                return false;
            }

            return true;
        }

        // MODIFIES: this.
        // EFFECT: Parse args into this Signature.
        // e.g. (a b . c) -> args = {"a", "b"}, vararg = "c"
        private void parse(Sexpr args) throws Exception {
            if (args instanceof Pair && ((Pair) args).getCar() instanceof Symbol) {
                this.args.add(((Pair) args).getCar().toString());
                this.parse(((Pair) args).getCdr());
            } else if (args instanceof Symbol) {
                this.vararg = ((Symbol) args).toString();
            } else if (args.type() == Type.Null) {
                return;
            } else {
                throw new Exception("Invalid Procedure Signature %s", args.toString());
            }
        }

        // MODIFIES: this.
        // EFFECT: Parse args into this Signature.
        // e.g. "a b . c" -> args = {"a", "b"}, vararg = "c"
        private void parse(java.lang.String args) throws Exception {
            java.lang.String[] vars = args.split(" ");

            for (int i = 0; i < vars.length; i++) {
                if (vars[i].equals(".")) {
                    if (vars.length != i + 2) {
                        throw new Exception("Invalid Procedure Signature %s", args);
                    } else {
                        this.vararg = vars[i + 1];
                    }
                    break;
                } else {
                    this.args.add(vars[i]);
                }
            }
        }

        // EFFECT: Returns the JSON representation of this Signature.
        public JSONObject toJson() {
            JSONArray args = new JSONArray();

            for (java.lang.String arg : this.args) {
                args.put(arg);
            }

            JSONObject obj = new JSONObject()
                    .put("type", "signature")
                    .put("args", args);

            if (this.vararg != null) {
                obj.put("vararg", this.vararg);
            }

            return obj;
        }

        // EFFECT: Creates and returns a new Signature based on the given JSON object.
        // Throws an Exception if the given JSON object doesn't represent a Procedure.
        public static Signature fromJson(JSONObject obj) throws Exception {
            if (obj.has("type") && obj.getString("type").equals("signature") && obj.has("args")) {
                List<java.lang.String> vars = new ArrayList<>();

                for (Object var : obj.getJSONArray("args")) {
                    if (var instanceof java.lang.String) {
                        vars.add((java.lang.String) var);
                    } else {
                        throw new Exception("cannot parse Signature from %s", obj);
                    }
                }

                java.lang.String vararg = null;

                if (obj.has("vararg")) {
                    vararg = obj.getString("vararg");
                }

                return new Signature(vars, vararg);
            } else {
                throw new Exception("cannot parse Signature from %s", obj);
            }
        }
    }
}
