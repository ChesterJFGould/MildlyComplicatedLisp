package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.*;

public class SexprTest {
    @Test
    void readTest() throws Exception {
        CharStream cs = new CharStream("(1 \"two\" three . 4.0)");
        assertEquals("(1 \"two\" three . 4.0)",
                Sexpr.read(cs).toString());

        CharStream hangingParen = new CharStream("(a))");

        assertThrows(Exception.class, () -> {
            Sexpr.read(hangingParen);
            Sexpr.read(hangingParen);
        });

        CharStream quote = new CharStream("'a");

        assertEquals("(quote a)", Sexpr.read(quote).toString());

        CharStream escapedQuote = new CharStream("\"\\\"\"");
        assertEquals("\"\"\"", Sexpr.read(escapedQuote).toString());

        CharStream nullCs = new CharStream("()");
        assertEquals(Type.Null, Sexpr.read(nullCs).type());
    }

    @Test
    void writeTest() {
        Sexpr oneTwo = new Pair(new Int(1), new Int(2));
        OutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        oneTwo.write(ps);

        assertEquals(os.toString(), "(1 . 2)");
    }

    @Test
    void fromJsonTest() throws Exception {
        assertEquals(new Bool(true).toJson().toString(), Sexpr.fromJson(new Environment(), new Bool(true).toJson()).toJson().toString());
        assertEquals(new Float(3.141).toJson().toString(), Sexpr.fromJson(new Environment(), new Float(3.141).toJson()).toJson().toString());
        assertEquals(new Int(10).toJson().toString(), Sexpr.fromJson(new Environment(), new Int(10).toJson()).toJson().toString());
        assertEquals(new Lambda(new Environment(), new Symbol("a"), new Symbol("a")).toJson().toString(),
                Sexpr.fromJson(new Environment(),
                        new Lambda(new Environment(), new Symbol("a"), new Symbol("a"))
                                .toJson()).toJson().toString());
        assertEquals(new Macro(new Environment(), new Symbol("a"), new Symbol("a")).toJson().toString(),
                Sexpr.fromJson(new Environment(),
                        new Macro(new Environment(), new Symbol("a"), new Symbol("a"))
                                .toJson()).toJson().toString());
        assertEquals(new Null().toJson().toString(), Sexpr.fromJson(new Environment(), new Null().toJson()).toJson().toString());
        assertEquals(new Pair(new Null(), new Null()).toJson().toString(),
                Sexpr.fromJson(new Environment(), new Pair(new Null(), new Null()).toJson()).toJson().toString());
        assertEquals(Procedure.newTypePredicate("int?", Type.Int).toJson().toString(),
                Sexpr.fromJson(new Environment(), Procedure.newTypePredicate("int?", Type.Int).toJson())
                        .toJson().toString());
        assertEquals(new String("42").toJson().toString(),
                Sexpr.fromJson(new Environment(), new String("42").toJson()).toJson().toString());
        assertEquals(new Symbol("(::)").toJson().toString(),
                Sexpr.fromJson(new Environment(), new Symbol("(::)").toJson()).toJson().toString());

        assertThrows(Exception.class, () -> Sexpr.fromJson(new Environment(), new Environment().toJson()));
        assertThrows(Exception.class, () -> Sexpr.fromJson(new Environment(), new JSONObject("{}")));
    }
}
