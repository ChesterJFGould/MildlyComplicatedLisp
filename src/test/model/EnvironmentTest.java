package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import org.json.*;

import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentTest {
    Environment a;
    Environment empty;
    Environment b;
    Environment c;
    Environment d;

    @BeforeEach
    void setup() {
        Environment.resetHeap();
        this.a = new Environment();
        this.empty = new Environment(a);
        this.b = new Environment(empty);
        this.c = new Environment();
        this.d = new Environment(c);

        this.a.put("a", new Symbol("a"));
        this.b.put("b", new Symbol("b"));
        this.c.put("c", new Symbol("c"));
        this.d.put("d", new Symbol("d"));
    }

    @Test
    void constructorGetPutTest() {
        assertTrue(this.b.get("a").equals(new Symbol("a")));
        assertTrue(this.b.get("b").equals(new Symbol("b")));
        assertNull(this.empty.get("b"));
    }

    @Test
    void setTest() {
        assertTrue(b.set("a", new Symbol("c")));
        assertTrue(a.get("a").equals(new Symbol("c")));
        assertFalse(empty.set("b", new Null()));
    }

    @Test
    void mergeTest() {
        this.d.merge(this.b);

        assertNull(this.d.get("d"));
        assertNull(this.d.get("c"));
        assertTrue(new Symbol("b").equals(this.d.get("b")));
    }

    @Test
    void mergeNonParentTest() {
        this.c.merge(this.a);

        assertTrue(this.c.get("a").equals(new Symbol("a")));
        assertNull(this.c.get("c"));
    }

    @Test
    void toJsonTest() {
        JSONObject aJSON = this.a.toJson();
        assertTrue(aJSON.has("type"));
        assertEquals("environment", aJSON.getString("type"));
        assertTrue(aJSON.has("vars"));
        assertEquals(new JSONArray().put(new JSONObject().put("key", "a").put("value", new Symbol("a").toJson())).toString(), aJSON.getJSONArray("vars").toString());
        Environment.resetSerializedTags();
        assertEquals("{\"parent\":{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"c\"},\"key\":\"c\"}],\"type\":\"environment\",\"ptr\":3},\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"d\"},\"key\":\"d\"}],\"type\":\"environment\",\"ptr\":4}",
                d.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        Environment.resetSerializedTags();
        assertEquals("{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"a\"},\"key\":\"a\"}],\"type\":\"environment\",\"ptr\":0}", a.toJson().toString());

        Environment.resetSerializedTags();
        assertEquals("{\"parent\":{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"c\"},\"key\":\"c\"}],\"type\":\"environment\",\"ptr\":3},\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"d\"},\"key\":\"d\"}],\"type\":\"environment\",\"ptr\":4}", d.toJson().toString());

        assertThrows(Exception.class, () -> Environment.fromJson(new Null().toJson()));

        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{\"type\":\"environment\",\"vars\":[1]}")));
    }
}
