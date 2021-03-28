package model;

import org.json.*;

// Represents a compound Procedure whos arguments get evaluated in the outer Environment.
public class Lambda extends Procedure {
    private Sexpr body;
    private Environment env;

    // MODIFIES: this
    // EFFECT: Initializes this Lambda with the given Environment,
    // Sexpr representation of a Signature, and body.
    public Lambda(Environment env, Sexpr signature, Sexpr body) throws Exception {
        super(signature, Procedure.evalWrapper((Environment ignored, Sexpr funArgs) -> {
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

            return body.eval(funEnv);
        }));

        this.env = env;
        this.body = body;
    }

    // MODIFIES: this
    // EFFECT: Initializes this Lambda with the given parameters.
    public Lambda(Environment env, Signature signature, Sexpr body) throws Exception {
        super(signature, Procedure.evalWrapper((Environment ignored, Sexpr funArgs) -> {
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

            return body.eval(funEnv);
        }));

        this.env = env;
        this.body = body;
    }

    // EFFECT: Returns the JSON representation of this Lambda.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "lambda")
                .put("signature", this.signature.toJson())
                .put("env", this.env.toJson())
                .put("body", this.body.toJson());
    }

    // EFFECT: Creates and returns a new Lambda based on the given JSON object.
    // Throws an Exception if the given JSON object doesn't represent a Lambda.
    public static Lambda fromJson(Environment env, JSONObject obj) throws Exception {
        if (obj.has("type")
                && obj.getString("type").equals("lambda")
                && obj.has("signature")
                && obj.has("body")
                && obj.has("env")) {
            Signature signature = Procedure.Signature.fromJson(obj.getJSONObject("signature"));
            Sexpr body = Sexpr.fromJson(env, obj.getJSONObject("body"));
            env = Environment.fromJson(obj.getJSONObject("env"));
            return new Lambda(env, signature, body);
        } else {
            throw new Exception("cannot parse Lambda from %s", obj);
        }
    }
}
