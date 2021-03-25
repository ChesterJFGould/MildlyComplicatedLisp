package model;

import org.json.*;

// Represents a compound Procedure whos arguments are not evaluated and whos
// return value is evaluated in the outer Environment.
public class Macro extends Procedure {
    private Sexpr body;
    private Environment env;

    // MODIFIES: this
    // EFFECT: Initializes this Macro with the given Environment,
    // Sexpr representation of a signature, and body.
    public Macro(Environment env, Sexpr signature, Sexpr body) throws Exception {
        super(signature, (Environment outerEnv, Sexpr funArgs) -> {
            Environment funEnv = new Environment(env);
            Sexpr arguments = signature;

            while (arguments.type() != Type.Null) {
                if (arguments instanceof Symbol) {
                    funEnv.put(((Symbol) arguments).getVal(), funArgs);
                    break;
                } else {
                    funEnv.put(((Symbol) ((Pair) arguments).getCar()).getVal(),
                            ((Pair) funArgs).getCar());
                    funArgs = ((Pair) funArgs).getCdr();
                    arguments = ((Pair) arguments).getCdr();
                }
            }

            return body.eval(funEnv).eval(outerEnv);
        });

        this.env = env;
        this.body = body;
    }

    // MODIFIES: this
    // EFFECT: Initializes this Macro with the given parameters.
    public Macro(Environment env, Signature signature, Sexpr body) throws Exception {
        super(signature, (Environment outerEnv, Sexpr funArgs) -> {
            Environment funEnv = new Environment(env);
            Sexpr arguments = signature.toSexpr();

            while (arguments.type() != Type.Null) {
                if (arguments instanceof Symbol) {
                    funEnv.put(((Symbol) arguments).getVal(), funArgs);
                    break;
                } else {
                    funEnv.put(((Symbol) ((Pair) arguments).getCar()).getVal(),
                            ((Pair) funArgs).getCar());
                    funArgs = ((Pair) funArgs).getCdr();
                    arguments = ((Pair) arguments).getCdr();
                }
            }

            return body.eval(funEnv).eval(outerEnv);
        });

        this.env = env;
        this.body = body;
    }

    // EFFECT: Returns the JSON representation of this Macro.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "macro")
                .put("signature", this.signature.toJson())
                .put("env", this.env.toJson())
                .put("body", body.toJson());
    }

    // EFFECT: Creates and returns a new Macro based on the given JSON object.
    // Throws an exception if the JSON object doesn't represent a Macro.
    public static Macro fromJson(Environment env, JSONObject obj) throws Exception {
        if (obj.has("type") &&
            obj.getString("type").equals("macro") &&
            obj.has("signature") &&
            obj.has("body") &&
            obj.has("env")) {
            return new Macro(Environment.fromJson(obj.getJSONObject("env")),
                             Procedure.Signature.fromJson(obj.getJSONObject("signature")),
                             Sexpr.fromJson(env, obj.getJSONObject("body")));
        } else {
            throw new Exception("cannot parse Macro from %s", obj);
        }
    }
}
