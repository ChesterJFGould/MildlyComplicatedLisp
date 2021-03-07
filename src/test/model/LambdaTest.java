package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import static org.junit.jupiter.api.Assertions.*;

public class LambdaTest {
    Lambda id;
    Lambda id2;

    @BeforeEach
    void setup() throws Exception {
        this.id = new Lambda(new Environment(), new Pair(new Symbol("a"), new Symbol("b")), new Symbol("a"));
        this.id2 = new Lambda(new Environment(), new Procedure.Signature("a . b"), new Symbol("a"));
    }

    @Test
    void constructorTest() throws Exception {
        assertTrue(this.id.apply(new Environment(), Pair.list(new Int(10))).equals(new Int(10)));
        assertTrue(this.id2.apply(new Environment(), Pair.list(new Float(10), new Int(10))).equals(new Float(10)));
    }

    @Test
    void toJsonTest() {
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"lambda\",\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", this.id.toJson().toString());
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"lambda\",\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", this.id2.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(this.id.toJson().toString(), Lambda.fromJson(new Environment(), this.id.toJson()).toJson().toString());
        assertEquals(this.id2.toJson().toString(), Lambda.fromJson(new Environment(), this.id2.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Lambda.fromJson(new Environment(), new Null().toJson()));
        assertThrows(Exception.class, () -> Lambda.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Lambda.fromJson(new JSONObject("{\"type\":\"lambda\"}")));
        assertThrows(Exception.class, () -> Lambda.fromJson(new JSONObject("{\"type\":\"lambda\",\"signature\":1}")));
    }
}
