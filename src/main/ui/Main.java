package ui;

import sexpr.Sexpr;
import ltreader.CharStream;

public class Main {
	public static void main(String[] args) {
		repl();
	}

	public static void repl() {
		System.out.print("> ");

		CharStream stdin = new CharStream(System.in);
		while (!stdin.done()) {

			System.out.flush();

			try {
				Sexpr expr = Sexpr.read(stdin);
				expr.eval().write(System.out);
				System.out.println("");
			} catch (sexpr.ParseException e) {
				System.out.println("Error : " + e.getMessage());
			}

			System.out.print("> ");
		}
		System.out.println("");
	}
}
