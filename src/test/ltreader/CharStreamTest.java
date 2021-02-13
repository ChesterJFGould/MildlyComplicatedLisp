package ltreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharStreamTest {
    CharStream a;
    CharStream bbSpaceA;
    CharStream testSpaceNewlineGood;

    @BeforeEach
    void setup() {
        this.a = new CharStream("a");
        this.bbSpaceA = new CharStream("bb a");
        this.testSpaceNewlineGood = new CharStream("test \ngood");
    }

    @Test
    void nextTest() {
        assertEquals('a', this.a.next());
        assertEquals('b', this.bbSpaceA.next());
        assertEquals('b', this.bbSpaceA.next());
        assertEquals(' ', this.bbSpaceA.next());
        assertEquals('a', this.bbSpaceA.next());
    }

    @Test
    void peekTest() {
        assertEquals('a', this.a.peek());
        assertEquals('a', this.a.peek());
        assertEquals('a', this.a.next());
    }

    @Test
    void peeksTest() {
        assertEquals("a", this.a.peeks());
        assertEquals("a", this.a.peeks());
        assertEquals('a', this.a.next());
    }

    @Test
    void readUntilTest() {
        assertEquals("bb", this.bbSpaceA.readUntil((Character c) -> c == ' '));
        assertEquals(" ", this.bbSpaceA.readUntil((Character c) -> c == 'a'));
        assertEquals("test \ngood", this.testSpaceNewlineGood.readUntil(
                (Character c) -> false));
    }

    @Test
    void eatWhitespaceTest() {
        assertEquals("test", this.testSpaceNewlineGood.readUntil(
                (Character c) -> Character.isWhitespace(c)));

        this.testSpaceNewlineGood.eatWhitespace();
        assertEquals('g', this.testSpaceNewlineGood.peek());
    }

    @Test
    void expectTest() throws Exception {
        this.a.expect('a');
        this.bbSpaceA.expect('b');
        assertThrows(Exception.class, () -> this.bbSpaceA.expect('a'));
        assertEquals('b', this.bbSpaceA.next());
        assertEquals(' ', this.bbSpaceA.next());
        assertThrows(Exception.class, () -> this.bbSpaceA.expect('b'));
        assertEquals('a', this.bbSpaceA.next());
    }

    @Test
    void doneTest() throws IOException {
        assertFalse(this.a.done());
        this.a.next();
        assertTrue(this.a.done());
        assertFalse(this.bbSpaceA.done());
        this.bbSpaceA.next();
        assertFalse(this.bbSpaceA.done());
        this.bbSpaceA.readUntil((Character c) -> false);
        assertTrue(this.bbSpaceA.done());

        // Force an IOException
        System.in.close();
        CharStream cs = new CharStream(System.in);

        assertTrue(cs.done());
    }
}
