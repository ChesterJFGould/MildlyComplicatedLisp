package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {
    Pair base;
    Pair oneTwo;
    Pair oneTwoThree;
    Pair addOneTwo;
    Environment env;

    @BeforeEach
    void setup() throws Exception {
        this.base = new Pair();
        this.oneTwo = new Pair(new Int(1), new Int(2));
        this.oneTwoThree = (Pair) Pair.list(new Int(1), new Int(2), new Int(3));
        this.addOneTwo = new Pair(new Symbol("+"), new Pair(new Int(1),
                new Pair(new Int(2), new Null())));

        this.env = new Environment();

        env.put("+", Procedure.newNumericBinaryOperator("+",
                (Long a, Long b) -> new Int(a + b),
                (Double a, Double b) -> new Float(a + b)));
    }

    @Test
    void constructorTest() {
        assertEquals(null, this.base.getCar());
        assertEquals(null, this.base.getCdr());

        assertTrue(this.oneTwo.getCar().equals(new Int(1)));
        assertTrue(this.oneTwo.getCdr().equals(new Int(2)));
    }

    @Test
    void setTest() {
        oneTwo.setCar(new Int(3));
        oneTwo.setCdr(new Int(4));

        assertTrue(oneTwo.getCar().equals(new Int(3)));
        assertTrue(oneTwo.getCdr().equals(new Int(4)));
    }

    @Test
    void toStringTest() {
        assertEquals("(1 . 2)", oneTwo.toString());
        assertEquals("(1 2 3)", oneTwoThree.toString());
    }

    @Test
    void evalTest() throws Exception {
        assertTrue(addOneTwo.eval(env).equals(new Int(3)));
        assertThrows(Exception.class, () -> oneTwoThree.eval(env));
    }

    @Test
    void typeTest() {
        assertEquals(Type.Pair, base.type());
        assertEquals(Type.Pair, oneTwo.type());
    }

    @Test
    void equalsTest() {
        assertTrue(this.base.equals(this.base));
        assertFalse(this.base.equals(new Pair()));

        assertTrue(this.oneTwo.equals(this.oneTwo));
        assertFalse(this.oneTwo.equals(new Pair(new Int(1), new Int(2))));
    }

    @Test
    void toJsonTest() {
        assertEquals("{\"cdr\":{\"type\":\"integer\",\"value\":2},\"car\":{\"type\":\"integer\",\"value\":1},\"type\":\"pair\"}", this.oneTwo.toJson().toString());
        assertEquals("{\"cdr\":{\"cdr\":{\"cdr\":{\"type\":\"null\"},\"car\":{\"type\":\"integer\",\"value\":3},\"type\":\"pair\"},\"car\":{\"type\":\"integer\",\"value\":2},\"type\":\"pair\"},\"car\":{\"type\":\"integer\",\"value\":1},\"type\":\"pair\"}", this.oneTwoThree.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(this.oneTwo.toJson().toString(), Pair.fromJson(new Environment(), this.oneTwo.toJson()).toJson().toString());
        assertEquals(this.oneTwoThree.toJson().toString(), Pair.fromJson(new Environment(), this.oneTwoThree.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Pair.fromJson(new Environment(), new Null().toJson()));
        assertThrows(Exception.class, () -> Pair.fromJson(new Environment(), new JSONObject("{}")));
        assertThrows(Exception.class, () -> Pair.fromJson(new Environment(), new JSONObject("{\"type\":\"pair\"}")));
    }
}
