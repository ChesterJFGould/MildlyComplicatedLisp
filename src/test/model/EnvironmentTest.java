package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;
import org.json.*;

import java.util.HashMap;

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
    void heapTest() {
        Environment.resetHeap();

        new Environment(5);
        new Environment(3);

        Environment.restoreHeapPointer();

        assertEquals(6, Environment.getHeap().getPtr());
    }

    @Test
    void constructorTest() {
        HashMap<java.lang.String, Sexpr> hm = new HashMap<>();
        hm.put("a", new Symbol("a"));
        assertEquals(hm.toString(), this.a.getVars().toString());
        assertEquals(this.a, this.empty.getParent());
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
    void removeTest() {
        this.a.remove("a");
        assertNull(this.a.get("a"));

        this.d.remove("c");
        assertNotNull(this.d.get("c"));
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
        JSONObject jsonO = a.toJson();
        Environment.resetSerializedTags();
        Environment.resetHeap();
        assertEquals("{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"a\"},\"key\":\"a\"}],\"type\":\"environment\",\"ptr\":0}", Environment.fromJson(jsonO).toJson().toString());

        Environment.resetSerializedTags();
        jsonO = d.toJson();
        Environment.resetSerializedTags();
        Environment.resetHeap();
        assertEquals("{\"parent\":{\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"c\"},\"key\":\"c\"}],\"type\":\"environment\",\"ptr\":3},\"vars\":[{\"value\":{\"type\":\"symbol\",\"value\":\"d\"},\"key\":\"d\"}],\"type\":\"environment\",\"ptr\":4}", Environment.fromJson(jsonO).toJson().toString());

        assertThrows(Exception.class, () -> Environment.fromJson(new Null().toJson()));

        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Environment.fromJson(new JSONObject("{\"type\":\"environment\",\"vars\":[1]}")));
    }
}
