package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringTest {
    String helloWorld;
    String sl;
    Environment env;

    @BeforeEach
    void setup() {
        this.helloWorld = new String("Hello, World!");
        this.sl = new String("Simple Lisp");
        this.env = new Environment();
    }

    @Test
    void constructorTest() {
        assertEquals("Hello, World!", this.helloWorld.getVal());
        assertEquals("Simple Lisp", this.sl.getVal());
    }

    @Test
    void toStringTest() {
        assertEquals("\"Hello, World!\"", this.helloWorld.toString());
        assertEquals("\"Simple Lisp\"", this.sl.toString());
    }

    @Test
    void evalTest() {
        assertEquals("\"Hello, World!\"", this.helloWorld.eval(env).toString());
        assertEquals("\"Simple Lisp\"", this.sl.eval(env).toString());
    }

    @Test
    void typeTest() {
        assertEquals(Type.String, helloWorld.type());
    }

    @Test
    void equalsTest() {
        assertTrue(helloWorld.equals(new String("Hello, World!")));
        assertTrue(helloWorld.equals(helloWorld));
        assertFalse(helloWorld.equals(sl));
        assertFalse(helloWorld.equals(new Null()));
    }
}
