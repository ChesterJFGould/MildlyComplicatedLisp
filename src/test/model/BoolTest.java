package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

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

    @Test
    void toJsonTest() {
        JSONObject tJSON = t.toJson();
        assertTrue(tJSON.has("type"));
        assertEquals("boolean", tJSON.getString("type"));
        assertTrue(tJSON.has("value"));
        assertEquals(true, tJSON.getBoolean("value"));

        JSONObject fJSON = f.toJson();
        assertTrue(fJSON.has("type"));
        assertEquals("boolean", fJSON.getString("type"));
        assertTrue(fJSON.has("value"));
        assertEquals(false, fJSON.getBoolean("value"));
    }

    @Test
    void fromJsonTest() throws Exception {
        JSONObject tJSON = t.toJson();
        assertTrue(t.equals(Bool.fromJson(tJSON)));

        JSONObject fJSON = f.toJson();
        assertTrue(f.equals(Bool.fromJson(fJSON)));

        assertThrows(Exception.class, () -> Bool.fromJson(new JSONObject().put("a", "b")));
    }
}
