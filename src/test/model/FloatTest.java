package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

import static org.junit.jupiter.api.Assertions.*;

public class FloatTest {
    Float ten;
    Float nTen;
    Float zero;
    Environment env;

    @BeforeEach
    void setup() {
        this.ten = new Float("10");
        this.nTen = new Float("-10");
        this.zero = new Float(0);
        this.env = new Environment();
    }

    @Test
    void constructorTest() {
        assertEquals(10, this.ten.getVal());
        assertEquals(-10, this.nTen.getVal());
        assertEquals(0, this.zero.getVal());
    }

    @Test
    void toStringTest() {
        assertEquals("10.0", this.ten.toString());
        assertEquals("-10.0", this.nTen.toString());
        assertEquals("0.0", this.zero.toString());
    }

    @Test
    void evalTest() {
        assertTrue(this.ten.eval(env).equals(new Float(10)));
        assertTrue(this.nTen.eval(env).equals(new Float(-10)));
    }

    @Test
    void typeTest() {
        assertEquals(Type.Float, this.ten.type());
        assertEquals(Type.Float, this.nTen.type());
        assertEquals(Type.Float, this.zero.type());
    }

    @Test
    void equalsTest() {
        assertTrue(this.ten.equals(new Float(10)));
        assertTrue(this.nTen.equals(new Float(-10)));
        assertTrue(this.zero.equals(new Float(0)));

        assertFalse(this.ten.equals(this.nTen));
        assertFalse(this.ten.equals(this.zero));
        assertFalse(this.zero.equals(new Null()));
    }

    @Test
    void toJsonTest() {
        assertEquals("{\"type\":\"float\",\"value\":10}", this.ten.toJson().toString());
        assertEquals("{\"type\":\"float\",\"value\":-10}", this.nTen.toJson().toString());
        assertEquals("{\"type\":\"float\",\"value\":0}", this.zero.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(this.ten.toJson().toString(), Float.fromJson(this.ten.toJson()).toJson().toString());
        assertEquals(this.nTen.toJson().toString(), Float.fromJson(this.nTen.toJson()).toJson().toString());
        assertEquals(this.zero.toJson().toString(), Float.fromJson(this.zero.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Float.fromJson(new Null().toJson()));
        assertThrows(Exception.class, () -> Float.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Float.fromJson(new JSONObject("{\"type\":\"float\"}")));
    }
}
