package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

import static org.junit.jupiter.api.Assertions.*;

public class    MacroTest {
    Macro id;
    Macro id2;

    @BeforeEach
    void setup() throws Exception {
        Environment.resetHeap();
        this.id = new Macro(new Environment(), new Pair(new Symbol("a"), new Symbol("b")), new Symbol("a"));
        this.id2 = new Macro(new Environment(), new Procedure.Signature("a . b"), new Symbol("a"));
    }

    @Test
    void constructorTest() throws Exception {
        assertTrue(this.id.apply(new Environment(), Pair.list(new Int(10))).equals(new Int(10)));
        assertTrue(this.id2.apply(new Environment(), Pair.list(new Float(10), new Int(10))).equals(new Float(10)));
    }

    @Test
    void toJsonTest() {
        Environment.resetSerializedTags();
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"macro\",\"env\":{\"vars\":[],\"type\":\"environment\",\"ptr\":0},\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", this.id.toJson().toString());
        Environment.resetSerializedTags();
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"macro\",\"env\":{\"vars\":[],\"type\":\"environment\",\"ptr\":1},\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", this.id2.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        Environment.resetSerializedTags();
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"macro\",\"env\":{\"type\":\"environment\",\"ptr\":0},\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", Macro.fromJson(new Environment(), this.id.toJson()).toJson().toString());
        Environment.resetSerializedTags();
        assertEquals("{\"signature\":{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"},\"type\":\"macro\",\"env\":{\"type\":\"environment\",\"ptr\":1},\"body\":{\"type\":\"symbol\",\"value\":\"a\"}}", Macro.fromJson(new Environment(), this.id2.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Macro.fromJson(new Environment(), new Null().toJson()));
        assertThrows(Exception.class, () -> Macro.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Macro.fromJson(new JSONObject("{\"type\":\"macro\"}")));
        assertThrows(Exception.class, () -> Macro.fromJson(new JSONObject("{\"type\":\"macro\", \"signature\":10}")));
    }
}
