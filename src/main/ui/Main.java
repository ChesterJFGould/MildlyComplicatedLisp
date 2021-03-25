package ui;

import model.*;
import persistence.*;

public class Main {
    static MainUI ui;

    // EFFECTS: Sets up the Simple Lisp environment and starts the repl.
    public static void main(java.lang.String[] args) throws model.Exception {
        model.Environment env = new model.Environment();

        initSimpleLisp(env);

        ui = new MainUI(env);

        repl(env);
    }

    // MODIFIES: env
    // EFFECTS: Starts a repl (Read, Eval, Print, Loop).
    public static void repl(model.Environment env) {
        System.out.print("> ");

        CharStream stdin = new CharStream(System.in);
        while (!stdin.done()) {
            try {
                Sexpr expr = Sexpr.read(stdin);
                expr.eval(env).write(System.out);
                System.out.println("");
            } catch (java.lang.Exception e) {
                System.out.println("Error : " + e.getMessage());
            }
            ui.update();
            System.out.print("> ");
        }
        System.out.println("");
    }

    // MODIFIES: env
    // EFFECT: Adds the true and false values to the given Environment.
    public static void initBools(Environment env) {
        env.put("true", new Bool(true));
        env.put("false", new Bool(false));
    }

    // MODIFIES: env
    // EFFECT: Adds the basic arithmetic operators to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initNumericOperators(Environment env) throws model.Exception {
        env.put("+", Procedure.newNumericBinaryOperator("+", (Long a, Long b) -> new Int(a + b),
                (Double a, Double b) -> new model.Float(a + b)));
        env.put("-", Procedure.newNumericBinaryOperator("-", (Long a, Long b) -> new Int(a - b),
                (Double a, Double b) -> new model.Float(a - b)));
        env.put("*", Procedure.newNumericBinaryOperator("*", (Long a, Long b) -> new Int(a * b),
                (Double a, Double b) -> new model.Float(a * b)));
        env.put("/", Procedure.newNumericBinaryOperator("/", (Long a, Long b) -> new Int(a / b),
                (Double a, Double b) -> new model.Float(a / b)));
    }

    // MODIFIES: env
    // EFFECT: Adds the basic numeric predicates to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initNumericPredicates(Environment env) throws model.Exception {
        env.put("<", Procedure.newNumericBinaryOperator("<", (Long a, Long b) -> new Bool(a < b),
                (Double a, Double b) -> new Bool(a < b)));
        env.put(">", Procedure.newNumericBinaryOperator(">", (Long a, Long b) -> new Bool(a > b),
                (Double a, Double b) -> new Bool(a > b)));
        env.put("=", Procedure.newNumericBinaryOperator("=", (Long a, Long b) -> new Bool(a == b),
                (Double a, Double b) -> new Bool(a == b)));
        env.put("<=", Procedure.newNumericBinaryOperator("<=", (Long a, Long b) -> new Bool(a <= b),
                (Double a, Double b) -> new Bool(a <= b)));
        env.put(">=", Procedure.newNumericBinaryOperator(">=", (Long a, Long b) -> new Bool(a >= b),
                (Double a, Double b) -> new Bool(a >= b)));
    }

    // MODIFIES: env
    // EFFECT: Adds the basic pair operators to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initPairOperators(Environment env) throws model.Exception {
        env.put("cons", Procedure.newBinaryOperator("cons", (Sexpr a, Sexpr b) -> {
            Pair pair = new Pair();
            pair.setCar(a);
            pair.setCdr(b);

            return pair;
        }));
        env.put("car", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCar()));
        env.put("cdr", Procedure.newPairUnaryOperator("car", (Pair pair) -> pair.getCdr()));
    }

    // MODIFIES: env
    // EFFECT: Adds the basic type predicates to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initTypePredicates(Environment env) throws model.Exception {
        env.put("float?", Procedure.newTypePredicate("float?", Type.Int));
        env.put("int?", Procedure.newTypePredicate("int?", Type.Int));
        env.put("null?", Procedure.newTypePredicate("null?", Type.Null));
        env.put("pair?", Procedure.newTypePredicate("pair?", Type.Pair));
        env.put("string?", Procedure.newTypePredicate("string?", Type.String));
        env.put("symbol?", Procedure.newTypePredicate("symbol?", Type.Symbol));
        env.put("bool?", Procedure.newTypePredicate("bool?", Type.Bool));
        env.put("procedure?", Procedure.newTypePredicate("procedure?", Type.Procedure));
    }

    // MODIFIES: env
    // EFFECT: Adds the def form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initDefForm(Environment env) throws model.Exception {
        env.put("def", new Procedure("def", "var val", (Environment defEnv, Sexpr defArgs) -> {
            Sexpr var = ((Pair) defArgs).getCar();
            if (var.type() != Type.Symbol) {
                throw new model.Exception("Invalid args to def form %s, variable must be a symbol", defArgs.toString());
            }
            defArgs = ((Pair) defArgs).getCdr();
            Sexpr val = ((Pair) defArgs).getCar().eval(env);

            defEnv.put(((Symbol) var).getVal(), val);

            return new Null();
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the set! form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initSetForm(Environment env) throws model.Exception {
        env.put("set!", new Procedure("set!", "var val", (Environment defEnv, Sexpr defArgs) -> {
            Sexpr var = ((Pair) defArgs).getCar();
            if (var.type() != Type.Symbol) {
                throw new model.Exception("Invalid args to set! form %s, variable must be a symbol",
                        defArgs.toString());
            }
            defArgs = ((Pair) defArgs).getCdr();
            Sexpr val = ((Pair) defArgs).getCar().eval(env);

            if (!defEnv.set(((Symbol) var).getVal(), val)) {
                throw new model.Exception("Undefined variable %s", var.toString());
            }

            return new Null();
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the simple equality predicate to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initSimpleEquality(Environment env) throws model.Exception {
        env.put("eq?", Procedure.newBinaryOperator("eq?", (Sexpr a, Sexpr b) -> new Bool(a.equals(b))));
    }

    // MODIFIES: env
    // EFFECT: Adds the pair setters to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initPairSetOperators(Environment env) throws model.Exception {
        env.put("set-car!", Procedure.newPairSetter("set-car!", (Pair pair, Sexpr val) -> {
            pair.setCar(val);
            return new Null();
        }));
        env.put("set-cdr!", Procedure.newPairSetter("set-cdr!", (Pair pair, Sexpr val) -> {
            pair.setCdr(val);
            return new Null();
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the if form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initIfForm(Environment env) throws model.Exception {
        env.put("if", new Procedure("if", "predicate consequence alternative", (Environment ifEnv, Sexpr ifArgs) -> {
            Sexpr pred = ((Pair) ifArgs).getCar().eval(ifEnv);
            ifArgs = ((Pair) ifArgs).getCdr();
            if ((pred.type() == Type.Bool) && !((Bool) pred).getVal()) {
                ifArgs = ((Pair) ifArgs).getCdr();
                return ((Pair) ifArgs).getCar().eval(ifEnv);
            } else {
                return ((Pair) ifArgs).getCar().eval(ifEnv);
            }
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the lambda form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initLambdaForm(Environment env) throws model.Exception {
        env.put("lambda", new Procedure("lambda", "arguments body", (Environment lamEnv, Sexpr lamArgs) -> {
            Sexpr signature = ((Pair) lamArgs).getCar();
            lamArgs = ((Pair) lamArgs).getCdr();
            Sexpr body = ((Pair) lamArgs).getCar();
            return new Lambda(lamEnv, signature, body);
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the macro form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initMacroForm(Environment env) throws model.Exception {
        env.put("macro", new Procedure("macro", "arguments body", (Environment lamEnv, Sexpr lamArgs) -> {
            Sexpr signature = ((Pair) lamArgs).getCar();
            lamArgs = ((Pair) lamArgs).getCdr();
            Sexpr body = ((Pair) lamArgs).getCar();
            return new Macro(lamEnv, signature, body);
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the quote form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initQuoteForm(Environment env) throws model.Exception {
        env.put("quote", new Procedure("quote", "obj", (Environment ignored, Sexpr args) -> {
            return ((Pair) args).getCar();
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the begin form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initBeginForm(Environment env) throws model.Exception {
        env.put("begin", new Procedure("begin", ". exprs", (Environment beginEnv, Sexpr args) -> {
            beginEnv = new Environment(env);

            Sexpr ret = new Null();

            while (args.type() != Type.Null) {
                ret = ((Pair) args).getCar().eval(beginEnv);
                args = ((Pair) args).getCdr();
            }

            return ret;
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the save form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initSaveForm(Environment env) throws model.Exception {
        env.put("save", new Procedure("save", "file", (Environment saveEnv, Sexpr args) -> {
            Sexpr pathExpr = ((Pair) args).getCar();
            if (!(pathExpr instanceof model.String)) {
                throw new model.Exception("Invalid arg to save form %s, file must be a string", args.toString());
            }

            java.lang.String path = ((model.String) pathExpr).getVal();

            JsonIO.write(saveEnv.toJson(), path);

            Environment.resetSerializedTags();
            Pair.resetSerializedTags();

            return new Null();
        }));
    }

    // MODIFIES: env
    // EFFECT: Adds the load form to the given Environment.
    // Doesn't actually throw an Exceptions but Java isn't smart enough to
    // figure that out.
    public static void initLoadForm(Environment env) throws model.Exception {
        env.put("load", new Procedure("load", "file", (Environment loadEnv, Sexpr args) -> {
            Sexpr pathExpr = ((Pair) args).getCar();
            if (!(pathExpr instanceof model.String)) {
                throw new model.Exception("Invalid arg to save form %s, file must be a string", args.toString());
            }

            Environment.resetHeap();
            Pair.resetHeap();

            java.lang.String path = ((model.String) pathExpr).getVal();

            Environment newEnv = Environment.fromJson(JsonIO.read(path));
            loadEnv.merge(newEnv);

            return new Null();
        }));
    }

    // MODIFIES: env
    // EFFECTS: Adds the base Simple Lisp constructs to env.
    public static void initSimpleLisp(Environment env) throws model.Exception {
        initBools(env);
        initNumericOperators(env);
        initNumericPredicates(env);
        initPairOperators(env);
        initTypePredicates(env);
        initSimpleEquality(env);
        initDefForm(env);
        initSetForm(env);
        initPairSetOperators(env);
        initIfForm(env);
        initLambdaForm(env);
        initMacroForm(env);
        initQuoteForm(env);
        initBeginForm(env);
        initSaveForm(env);
        initLoadForm(env);
    }
}
