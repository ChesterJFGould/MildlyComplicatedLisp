package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

import static org.junit.jupiter.api.Assertions.*;

public class ProcedureTest {
    public static class SignatureTests {
        Procedure.Signature empty;
        Procedure.Signature a;
        Procedure.Signature ab;
        Procedure.Signature dotB;
        Procedure.Signature aDotB;

        @BeforeEach
        void setup() throws Exception {
            this.empty = new Procedure.Signature(new Null());
            this.a = new Procedure.Signature("a");
            this.ab = new Procedure.Signature(Pair.list(new Symbol("a"), new Symbol("b")));
            this.dotB = new Procedure.Signature(new Symbol("b"));
            this.aDotB = new Procedure.Signature("a . b");
        }

        @Test
        void constructorParseValidateTest() throws Exception {
            assertThrows(Exception.class, () -> new Procedure.Signature("a . b c"));
            assertThrows(Exception.class, () -> new Procedure.Signature(new Float(10)));

            Sexpr noArgs = new Null();
            Sexpr oneArg = new Pair(new Int(1), noArgs);
            Sexpr twoArgs = new Pair(new Float(2), oneArg);
            Sexpr threeArgs = new Pair(new String("3"), twoArgs);
            Sexpr fourArgs = new Pair(new Symbol("4"), twoArgs);

            assertTrue(this.empty.validate(noArgs));
            assertFalse(this.empty.validate(oneArg));

            assertTrue(this.a.validate(oneArg));
            assertFalse(this.a.validate(noArgs));
            assertFalse(this.a.validate(twoArgs));

            assertTrue(this.ab.validate(twoArgs));
            assertFalse(this.ab.validate(oneArg));
            assertFalse(this.ab.validate(threeArgs));

            assertTrue(this.dotB.validate(noArgs));
            assertTrue(this.dotB.validate(oneArg));
            assertTrue(this.dotB.validate(twoArgs));
            assertTrue(this.dotB.validate(threeArgs));
            assertTrue(this.dotB.validate(fourArgs));

            assertTrue(this.aDotB.validate(oneArg));
            assertTrue(this.aDotB.validate(twoArgs));
            assertTrue(this.aDotB.validate(threeArgs));
            assertFalse(this.aDotB.validate(noArgs));

            assertThrows(Exception.class, () -> new Procedure.Signature(new Int(10)));
        }

        @Test
        void toStringTest() {
            assertEquals("()", this.empty.toString());
            assertEquals("(a)", this.a.toString());
            assertEquals("(a b)", this.ab.toString());
            assertEquals("( . b)", this.dotB.toString());
            assertEquals("(a . b)", this.aDotB.toString());
        }

        @Test
        void toSexprTest() {
            assertEquals("()", this.empty.toSexpr().toString());
            assertEquals("(a)", this.a.toSexpr().toString());
            assertEquals("(a . b)", this.aDotB.toSexpr().toString());
        }

        @Test
        void toJsonTest() {
            assertEquals("{\"args\":[\"a\",\"b\"],\"type\":\"signature\"}", this.ab.toJson().toString());
            assertEquals("{\"args\":[\"a\"],\"vararg\":\"b\",\"type\":\"signature\"}", this.aDotB.toJson().toString());
        }

        @Test
        void fromJsonTest() throws Exception {
            assertEquals(this.ab.toJson().toString(), Procedure.Signature.fromJson(this.ab.toJson()).toJson().toString());
            assertEquals(this.aDotB.toJson().toString(), Procedure.Signature.fromJson(this.aDotB.toJson()).toJson().toString());

            assertThrows(Exception.class, () -> Procedure.Signature.fromJson(new Null().toJson()));
            assertThrows(Exception.class, () -> Procedure.Signature.fromJson(new JSONObject("{\"args\":[\"a\", 10],\"type\":\"signature\"}")));
        }
    }

    @Test
    void contructorApplyTest() throws Exception {
        Environment env = new Environment();

        Procedure ident = new Procedure(Pair.list(new Symbol("a")),
                (Environment identEnv, Sexpr args) -> ((Pair) args).getCar().eval(identEnv));

        env.put("a", new Float(3.141));

        assertTrue(ident.apply(env, Pair.list(new Symbol("a"))).equals(new Float(3.141)));
        assertThrows(Exception.class,
                () -> ident.apply(env, Pair.list(new Symbol("a"), new Null())));
        assertThrows(Exception.class,
                () -> ident.apply(env, new Int(1)));
    }

    @Test
    void evalTest() throws Exception {
        Procedure ident = new Procedure(Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));

        assertEquals(ident, ident.eval(new Environment()));
    }

    @Test
    void toStringTest() throws Exception {
        Procedure ident = new Procedure(Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));
        Procedure identVarargs = new Procedure(new Symbol("a"),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));

        assertEquals("<Procedure (a)>", ident.toString());
        assertEquals("<Procedure ( . a)>", identVarargs.toString());
    }

    @Test
    void typeTest() throws Exception {
        Procedure ident = new Procedure(Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));

        assertEquals(Type.Procedure, ident.type());
    }

    @Test
    void equalsTest() throws Exception {
        Procedure ident1 = new Procedure(Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));
        Procedure ident2 = new Procedure(Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));

        assertTrue(ident1.equals(ident1));
        assertFalse(ident1.equals(ident2));
    }

    @Test
    void evalWrapperTest() throws Exception {
        Procedure ident = new Procedure("test", "a", Procedure.evalWrapper(
                (Environment env, Sexpr args) -> ((Pair) args).getCar()));

        Environment env = new Environment();
        env.put("e", new Float(2.718));

        assertTrue(ident.apply(env, Pair.list(new Symbol("e"))).equals(new Float(2.718)));
    }

    @Test
    void numBinOpTest() throws Exception {
        Procedure add = Procedure.newNumericBinaryOperator("+",
                (Long a, Long b) -> new Int(a + b),
                (Double a, Double b) -> new Float(a + b));

        Environment env = new Environment();

        assertTrue(add.apply(env, Pair.list(new Int(3), new Int(2))).equals(new Int(5)));
        assertTrue(add.apply(env, Pair.list(new Float(3), new Float(2))).equals(new Float(5)));

        assertThrows(Exception.class, () -> add.apply(env, Pair.list(new Int(3), new String("2"))));
        assertThrows(Exception.class, () -> add.apply(env, Pair.list(new Float(3), new Null())));
        assertThrows(Exception.class, () -> add.apply(env, Pair.list(new String("2"), new Int(3))));
        assertThrows(Exception.class, () -> add.apply(env, Pair.list(new Null(), new Float(3))));
    }

    @Test
    void binOpTest() throws Exception {
        Procedure cons = Procedure.newBinaryOperator("test",
                (Sexpr a, Sexpr b) -> new Pair(a, b));

        Environment env = new Environment();

        assertEquals("(1 . 2)",
                cons.apply(env, Pair.list(new Int(1), new Int(2))).toString());
    }

    @Test
    void typePredTest() throws Exception {
        Procedure intTest = Procedure.newTypePredicate("test", Type.Int);
        Procedure floatTest = Procedure.newTypePredicate("test", Type.Float);

        Environment env = new Environment();

        assertTrue(intTest.apply(env, Pair.list(new Int(1729))).equals(new Bool(true)));
        assertTrue(intTest.apply(env, Pair.list(new Float(3.14))).equals(new Bool(false)));

        assertTrue(floatTest.apply(env, Pair.list(new Float(1729))).equals(new Bool(true)));
        assertTrue(intTest.apply(env, Pair.list(new String("1^3 + 12^3 = 9^3 + 10^3"))).equals(new Bool(false)));
    }

    @Test
    void pairUnOpTest() throws Exception {
        Procedure car = Procedure.newPairUnaryOperator("car",
                (Pair p) -> p.getCar());

        Environment env = new Environment();
        env.put("quote", new Procedure("test", "a",
                (Environment ignored, Sexpr args) -> ((Pair) args).getCar()));

        Pair pair = (Pair) Pair.list(new Symbol("quote"), new Pair(new Int(1), new Int(2)));
        assertTrue(car.apply(env, Pair.list(pair)).equals(new Int(1)));
        assertThrows(Exception.class, () -> car.apply(new Environment(), Pair.list(new Null())));
    }

    @Test
    void pairSetterTest() throws Exception {
        Procedure setCar = Procedure.newPairSetter("set-car!",
                (Pair p, Sexpr val) -> {
                    p.setCar(val);
                    return new Null();
                });

        Environment env = new Environment();
        env.put("quote", new Procedure("test", "a",
                (Environment ignored, Sexpr args) -> ((Pair) args).getCar()));

        Pair pair = new Pair(new Int(1), new Int(2));
        setCar.apply(env, Pair.list(Pair.list(new Symbol("quote"), pair), new Float(3)));
        assertTrue(pair.getCar().equals(new Float(3)));
        assertThrows(Exception.class, () -> setCar.apply(env, Pair.list(new Int(10), Pair.list(new Symbol("quote"), pair))));
    }

    @Test
    void getProcedureTest() throws Exception {
        Procedure ident = new Procedure("id", Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));

        assertEquals(ident, Procedure.getProcedure("id"));
    }

    @Test
    void toJsonTest() throws Exception {
        Procedure ident = new Procedure("id", Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));
        assertEquals("{\"name\":\"id\",\"type\":\"procedure\"}", ident.toJson().toString());
    }

    @Test
    void fromJsonTest() throws Exception {
        Procedure ident = new Procedure("id", Pair.list(new Symbol("a")),
                (Environment env, Sexpr args) -> ((Pair) args).getCar().eval(env));
        assertEquals(ident.toJson().toString(), Procedure.fromJson(ident.toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Procedure.fromJson(new Null().toJson()));
        assertThrows(Exception.class, () -> Procedure.fromJson(new JSONObject("{\"name\":\"a\",\"type\":\"procedure\"}")));
    }
}
