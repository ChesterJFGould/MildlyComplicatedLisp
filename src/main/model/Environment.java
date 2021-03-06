package model;

import java.util.HashMap;

import org.json.*;

import java.util.HashMap;
import java.util.Map;

// Represents the environment an s-expression is evaluated in.
public class Environment {
    private Map<java.lang.String, Sexpr> vars;
    private Environment parent;
    private long ptr;
    private boolean serialized;

    private static Heap<Environment> heap = new Heap<>();

    // MODIFIES: heap
    // EFFECT: Replaces heap with a new heap.
    public static void resetHeap() {
        heap = new Heap<>();
    }

    // MODIFIES: heap
    // EFFECT: Sets the heap ptr to be higher than all entries it contains.
    public static void restoreHeapPointer() {
        long max = 0;
        for (HashMap.Entry<Long, Environment> entry : heap.getHeap().entrySet()) {
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
        for (HashMap.Entry<Long, Environment> entry : heap.getHeap().entrySet()) {
            entry.getValue().serialized = false;
        }
    }

    // EFFECT: Returns the heap.
    public static Heap<Environment> getHeap() {
        return Environment.heap;
    }

    // MODIFIES: this
    // EFFECT: Creates a new empty Environment.
    public Environment() {
        this.vars = new HashMap<>();
        this.parent = null;
        this.serialized = false;
        this.ptr = Environment.heap.malloc(this);
    }

    // MODIFIES: this
    // EFFECT: Initializes this Environment with the given pointer.
    public Environment(long ptr) {
        this.vars = new HashMap<>();
        this.parent = null;
        this.serialized = false;
        this.ptr = ptr;
        heap.put(ptr, this);
    }

    // MODIFIES: this
    // EFFECT: Creates a new empty Environment with the given Environment as a parent.
    public Environment(Environment parent) {
        this.vars = new HashMap<>();
        this.parent = parent;
        this.serialized = false;
        this.ptr = Environment.heap.malloc(this);
    }

    public Map<java.lang.String, Sexpr> getVars() {
        return this.vars;
    }

    public Environment getParent() {
        return this.parent;
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

    public void remove(java.lang.String key) {
        this.vars.remove(key);
    }

    // MODIFIES: this
    // EFFECT: Puts all the variables in the given Environment into this Environment,
    // then does the same with the parents if they exist.
    public void merge(Environment env) {
        this.vars = env.vars;
        this.ptr = env.ptr;
        heap.put(this.ptr, this);

        if (env.parent != null) {
            if (this.parent == null) {
                this.parent = env.parent;
            } else {
                this.parent.merge(env.parent);
            }
        }
    }

    // EFFECT: Returns the JSON representation of this Environment.
    public JSONObject toJson() {
        Environment env = heap.get(this.ptr);
        if (env != null && env.serialized) {
            return new JSONObject()
                    .put("type", "environment")
                    .put("ptr", this.ptr);
        }

        this.serialized = true;
        if (env != null) {
            env.serialized = true;
        }

        JSONArray vars = new JSONArray();

        for (HashMap.Entry<java.lang.String, Sexpr> entry : this.vars.entrySet()) {
            vars.put(new JSONObject().put("key", entry.getKey()).put("value", entry.getValue().toJson()));
        }

        JSONObject obj = new JSONObject()
                .put("type", "environment")
                .put("ptr", this.ptr)
                .put("vars", vars);

        if (this.parent != null) {
            obj.put("parent", this.parent.toJson());
        }

        return obj;
    }

    // EFFECT: Creates and returns an Environment based on the given JSON object.
    // Throws an Exception if the JSON object doesn't represent an Environment.
    public static Environment fromJson(JSONObject obj) throws Exception {
        if (!obj.has("type") || !obj.getString("type").equals("environment") || !obj.has("ptr")) {
            throw new Exception("cannot parse Environment from %s", obj);
        }

        long ptr = obj.getLong("ptr");

        Environment env = Environment.heap.get(ptr);

        if (env != null) {
            return env;
        } else if (!obj.has("vars")) {
            throw new Exception("cannot parse Environment from %s", obj);
        }

        HashMap<java.lang.String, Sexpr> vars = new HashMap<>();

        env = new Environment(ptr);

        for (Object var : obj.getJSONArray("vars")) {
            if (var instanceof JSONObject) {
                vars.put(((JSONObject) var).getString("key"),
                        Sexpr.fromJson(env, ((JSONObject) var).getJSONObject("value")));
            }
        }

        env.vars = vars;

        if (obj.has("parent")) {
            env.parent = Environment.fromJson(obj.getJSONObject("parent"));
        }


        return env;
    }
}
