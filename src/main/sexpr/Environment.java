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

	public void put(java.lang.String key, Sexpr val) {
		this.vars.put(key, val);
	}
}
