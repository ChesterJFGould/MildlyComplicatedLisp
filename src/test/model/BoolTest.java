package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoolTest {
    Bool t;
    Bool f;
    Environment env;

    @BeforeEach
    void setup() {
        this.t = new Bool(true);
        this.f = new Bool(false);
        this.env = new Environment();
    }

    @Test
    void constructorTest() {
        assertTrue(t.getVal());
        assertFalse(f.getVal());
    }

    @Test
    void toStringTest() {
        assertEquals("true", t.toString());
        assertEquals("false", f.toString());
    }

    @Test
    void typeTest() {
        assertEquals(t.type(), Type.Bool);
        assertEquals(f.type(), Type.Bool);
    }

    @Test
    void evalTest() {
        assertEquals(t.eval(this.env), t);
        assertEquals(f.eval(this.env), f);
    }

    @Test
    void equalityTest() {
        assertTrue(t.equals(new Bool(true)));
        assertFalse(t.equals(f));
        assertFalse(t.equals(new Null()));

        assertTrue(f.equals(new Bool(false)));
        assertFalse(f.equals(t));
        assertFalse(f.equals(new Null()));
    }
}
