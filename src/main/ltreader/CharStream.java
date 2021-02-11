package ltreader;

import java.io.InputStream;
import java.io.IOException;
import java.util.function.Predicate;

public class CharStream {
	private InputStream inStream;
	private char current;
	private boolean done;

	public CharStream(InputStream inStream) {
		this.done = false;
		this.inStream = inStream;
		this.next();
	}

	public char next() {
		char now = this.current;
		try {
			int tmp = inStream.read();
			if (tmp == -1) {
				this.done = true;
			} else {
				this.current = (char)tmp;
			}
		} catch (IOException e) {
			this.done = true;
		}

		return now;
	}

	public char peek() {
		return this.current;
	}

	public String peeks() {
		return Character.toString(this.current);
	}

	public String readUntil(Predicate<Character> delimPred) {
		String acc = "";

		for (char c = this.peek(); !this.done() && !delimPred.test(c); c = this.peek()) {
			acc += Character.toString(c);
			this.next();
		}

		return acc;
	}

	public void eatWhitespace() {
		while (Character.isWhitespace(this.peek())) {
			this.next();
		}
	}

	public void expect(char c) throws sexpr.Exception {
		if (this.peek() != c) {
			throw new sexpr.Exception("Expected %c but instead found %c", c, this.peek());
		}
		this.next();
	}

	public boolean done() {
		return this.done;
	}
}
