package model;

import org.json.*;

public class Lambda extends Procedure {
    private Sexpr body;

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

        this.body = body;
    }

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

        this.body = body;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "lambda")
                .put("signature", this.signature.toJson())
                .put("body", body.toJson());
    }

    public static Lambda fromJson(Environment env, JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("lambda") && obj.has("signature") && obj.has("body")) {
            return new Lambda(env, Procedure.Signature.fromJson(obj.getJSONObject("signature")),
                              Sexpr.fromJson(env, obj.getJSONObject("body")));
        } else {
            throw new Exception("cannot parse Lambda from %s", obj);
        }
    }
}
