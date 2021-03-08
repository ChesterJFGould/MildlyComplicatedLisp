package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

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

    @Test
    void toJsonTest() {
        assertEquals("{\"type\":\"string\",\"value\":\"Hello, World!\"}", this.helloWorld.toJson().toString());
        assertEquals("{\"type\":\"string\",\"value\":\"Simple Lisp\"}", this.sl.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(this.helloWorld.toJson().toString(), String.fromJson(this.helloWorld.toJson()).toJson().toString());
        assertEquals(this.sl.toJson().toString(), String.fromJson(this.sl.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> String.fromJson(new Null().toJson()));
        assertThrows(Exception.class, () -> String.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> String.fromJson(new JSONObject("{\"type\":\"string\"}")));
    }
}
