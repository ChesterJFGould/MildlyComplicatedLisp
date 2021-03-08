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
        assertEquals(null, this.empty.get("b"));
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

        assertTrue(d.get("a").equals(new Symbol("a")));
        assertTrue(d.get("b").equals(new Symbol("b")));
        assertTrue(d.get("c").equals(new Symbol("c")));
        assertTrue(d.get("d").equals(new Symbol("d")));
        assertEquals(null, a.get("z"));
    }

    @Test
    void mergeNonParentTest() {
        this.c.merge(this.a);

        assertTrue(this.c.get("a").equals(new Symbol("a")));
        assertTrue(this.c.get("c").equals(new Symbol("c")));
    }

    @Test
    void toJsonTest() {
        JSONObject aJSON = this.a.toJson();
        assertTrue(aJSON.has("type"));
        assertEquals("environment", aJSON.getString("type"));
        assertTrue(aJSON.has("vars"));
        assertEquals(new JSONArray().put(new JSONObject().put("key", "a").put("value", new Symbol("a").toJson())).toString(), aJSON.getJSONArray("vars").toString());

        assertEquals("{\"parent\":{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"c\"},"
                        + "\"key\":\"c\"}],\"type\":\"environment\"},\"vars\":"
                        + "[{\"value\":{\"type\":\"symbol\",\"value\":\"d\"},\"key\":\"d\"}],\"type\":\"environment\"}",
                d.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(a.toJson().toString(), Environment.fromJson(a.toJson()).toJson().toString());

        assertEquals(d.toJson().toString(), Environment.fromJson(d.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Environment.fromJson(new Null().toJson()));

        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{\"type\":\"environment\",\"vars\":[1]}")));
    }
}
