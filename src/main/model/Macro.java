package model;

import org.json.*;

public class Macro extends Procedure {
    private Sexpr body;

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

        this.body = body;
    }

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

        this.body = body;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "macro")
                .put("signature", this.signature.toJson())
                .put("body", body.toJson());
    }

    public static Macro fromJson(Environment env, JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("macro") && obj.has("signature") && obj.has("body")) {
            return new Macro(env, Procedure.Signature.fromJson(obj.getJSONObject("signature")),
                             Sexpr.fromJson(env, obj.getJSONObject("body")));
        } else {
            throw new Exception("cannot parse Macro from %s", obj);
        }
    }
}
