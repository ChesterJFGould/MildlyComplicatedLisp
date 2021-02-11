package sexpr;

import java.util.HashMap;

public class Environment {
	private HashMap<java.lang.String, Sexpr> vars;
	private Environment parent;

	public Environment() {
		this.vars = new HashMap<java.lang.String, Sexpr>();
		this.parent = null;
	}

	public Environment(Environment parent) {
		this.vars = new HashMap<java.lang.String, Sexpr>();
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
}
