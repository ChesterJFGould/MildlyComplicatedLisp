package ui;

import sexpr.Sexpr;
import ltreader.CharStream;
import sexpr.*;

public class Main {
	public static void main(java.lang.String[] args) throws sexpr.Exception {
		sexpr.Environment env = new sexpr.Environment();

		env.put("+", new sexpr.Procedure("a b", (Environment env1, Sexpr args1) -> {
			Sexpr a = ((Pair)args1).getCar().eval(env1);
			Sexpr b = ((Pair)((Pair)args1).getCdr()).getCar().eval(env1);

			if ((a instanceof Int) && (b instanceof Int)) {
				return ((Int)a).add((Int)b);
			} else {
				throw new sexpr.Exception("Invalid arguments");
			}
		}));

		repl(env);
	}

	public static void repl(sexpr.Environment env) {
		System.out.print("> ");

		CharStream stdin = new CharStream(System.in);
		while (!stdin.done()) {
			try {
				Sexpr expr = Sexpr.read(stdin);
				expr.eval(env).write(System.out);
				System.out.println("");
			} catch (sexpr.Exception e) {
				System.out.println("Error : " + e.getMessage());
			}

			System.out.print("> ");
		}
		System.out.println("");
	}
}
