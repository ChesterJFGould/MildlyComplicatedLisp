package model;

import java.io.InputStream;
import java.io.IOException;
import java.util.function.Predicate;
import java.io.ByteArrayInputStream;

// Represents a stream of chars. Contains some nice methods to make parsing
// easier (eatWhitespace, readUntil, etc.).
public class CharStream {
    private InputStream inStream;
    private char current;
    private boolean done;

    // MODIFIES: this
    // EFFECT: Initializes a new CharStream using the InputStream as input.
    public CharStream(InputStream inStream) {
        this.done = false;
        this.inStream = inStream;
        this.next();
    }

    // MODIFIES: this
    // EFFECT: Initializes a new CharStream using the String as input.
    public CharStream(java.lang.String s) {
        this(new ByteArrayInputStream(s.getBytes()));
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
    public java.lang.String peeks() {
        return Character.toString(this.current);
    }

    // MODIFIES: this.
    // EFFECT: Returns all the characters until delimPred returns true.
    public java.lang.String readUntil(Predicate<Character> delimPred) {
        java.lang.String acc = "";

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
    public void expect(char c) throws Exception {
        if (this.done()) {
            throw new Exception("Unexpected EOF, expected %c", c);
        }
        if (this.peek() != c) {
            throw new Exception("Expected %c but instead found %c", c, this.peek());
        }
        this.next();
    }

    // EFFECT: Returns done.
    public boolean done() {
        return this.done;
    }
}
