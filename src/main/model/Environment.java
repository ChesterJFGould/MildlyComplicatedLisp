package model;

import java.util.HashMap;

import org.json.*;

// Represents the environment an s-expression is evaluated in.
public class Environment {
    private HashMap<java.lang.String, Sexpr> vars;
    private Environment parent;

    public Environment() {
        this.vars = new HashMap<>();
        this.parent = null;
    }

    public Environment(Environment parent) {
        this.vars = new HashMap<>();
        this.parent = parent;
    }

    // EFFECT: Returns the Sexpr associated with key in vars or null if it doesn't
    // exist. If key is not in vars and parent isn't null try to get the Sexpr from
    // parent, else return null.
    public Sexpr get(java.lang.String key) {
        Sexpr val = vars.get(key);
        if (val == null) {
            if (this.parent == null) {
                return null;
            } else {
                return parent.get(key);
            }
        } else {
            return val;
        }
    }

    // MODIFIES: this.
    // EFFECT: Associates val with key in vars.
    public void put(java.lang.String key, Sexpr val) {
        this.vars.put(key, val);
    }

    // MODIFIES: this.
    // EFFECT: Tries to associate key with val in vars. If key is not in vars and
    // parent isn't null tries to associate key with val in parent. Returns true if
    // successful, false if key is not in vars.
    public boolean set(java.lang.String key, Sexpr val) {
        Sexpr prev = this.vars.replace(key, val);
        if (prev == null) {
            if (this.parent != null) {
                return this.parent.set(key, val);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void merge(Environment env) {
        for (HashMap.Entry<java.lang.String, Sexpr> entry : env.vars.entrySet()) {
            this.vars.put(entry.getKey(), entry.getValue());
        }

        if (env.parent != null) {
            if (this.parent == null) {
                this.parent = env.parent;
            } else {
                this.parent.merge(env.parent);
            }
        }
    }

    public JSONObject toJson() {
        JSONArray vars = new JSONArray();

        for (HashMap.Entry<java.lang.String, Sexpr> entry : this.vars.entrySet()) {
            vars.put(new JSONObject().put("key", entry.getKey()).put("value", entry.getValue().toJson()));
        }

        JSONObject obj = new JSONObject()
                .put("type", "environment")
                .put("vars", vars);

        if (this.parent != null) {
            obj.put("parent", this.parent.toJson());
        }

        return obj;
    }

    public static Environment fromJson(JSONObject obj) throws Exception {
        if (!(obj.has("type") && obj.getString("type").equals("environment"))) {
            throw new Exception("cannot parse Environment from %s", obj);
        }

        HashMap<java.lang.String, Sexpr> vars = new HashMap<>();

        Environment env = new Environment();

        for (Object var : obj.getJSONArray("vars")) {
            if (var instanceof JSONObject) {
                vars.put(((JSONObject) var).getString("key"),
                         Sexpr.fromJson(env, ((JSONObject) var).getJSONObject("value")));
            }
        }

        if (obj.has("parent")) {
            env.parent = Environment.fromJson(obj.getJSONObject("parent"));
        }

        env.vars = vars;

        return env;
    }
}
