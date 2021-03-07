package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NullTest {
    Null n;

    @BeforeEach
    void constructorTest() {
        this.n = new Null();
    }

    @Test
    void toStringTest() {
        assertEquals("()", n.toString());
    }

    @Test
    void typeTest() {
        assertEquals(Type.Null, n.type());
    }

    @Test
    void equalsTest() {
        assertTrue(this.n.equals(n));
        assertTrue(this.n.equals(new Null()));
        assertFalse(this.n.equals(new Float(10)));
    }

    @Test
    void evalTest() {
        assertTrue(n.eval(new Environment()).equals(new Null()));
    }

    @Test
    void toJsonTest() {
        assertEquals("{\"type\":\"null\"}", n.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(n.toJson().toString(), Null.fromJson(n.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Null.fromJson(new Int(1729).toJson()));
    }
}
