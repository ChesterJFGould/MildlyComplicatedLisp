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

    // MODIFIES: this.
    // EFFECT: Returns current and sets current to the next char in inStream.
    // If inStream is EOF or encounters an error set done to true.
    public char next() {
        char now = this.current;
        try {
            int tmp = inStream.read();
            if (tmp == -1) {
                this.done = true;
            } else {
                this.current = (char) tmp;
            }
        } catch (IOException e) {
            this.done = true;
        }

        return now;
    }

    // EFFECT: Returns current.
    public char peek() {
        return this.current;
    }

    // EFFECT: Returns current converted to a string.
    public String peeks() {
        return Character.toString(this.current);
    }

    // MODIFIES: this.
    // EFFECT: Returns all the characters until delimPred returns true.
    public String readUntil(Predicate<Character> delimPred) {
        String acc = "";

        for (char c = this.peek(); !this.done() && !delimPred.test(c); c = this.peek()) {
            acc += Character.toString(c);
            this.next();
        }

        return acc;
    }

    // MODIFIES: this.
    // EFFECT: Eats all the pending whitespace.
    public void eatWhitespace() {
        while (Character.isWhitespace(this.peek())) {
            this.next();
        }
    }

    // MODIFIES: this.
    // EFFECT: If the next char isn't equal to c throws an exception.
    public void expect(char c) throws sexpr.Exception {
        if (this.peek() != c) {
            throw new sexpr.Exception("Expected %c but instead found %c", c, this.peek());
        }
        this.next();
    }

    // EFFECT: Returns done.
    public boolean done() {
        return this.done;
    }
}
