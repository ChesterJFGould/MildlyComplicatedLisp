package sexpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ltreader.CharStream;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;

public class SexprTest {
	@Test
	void readTest() throws Exception {
		CharStream cs = new CharStream(new StringBufferInputStream("(1 \"two\" three . 4.0)"));
		assertEquals("(1 \"two\" three . 4.0)",
			Sexpr.read(cs).toString());

		CharStream hangingParen = new CharStream(new StringBufferInputStream("(a))"));

		assertThrows(Exception.class, () -> {Sexpr.read(hangingParen); Sexpr.read(hangingParen);});

		CharStream quote = new CharStream(new StringBufferInputStream("'a"));

		assertEquals("(quote a)", Sexpr.read(quote).toString());

		CharStream escapedQuote = new CharStream(new StringBufferInputStream("\"\\\"\""));
		assertEquals("\"\"\"", Sexpr.read(escapedQuote).toString());

		CharStream nullCs = new CharStream(new StringBufferInputStream("()"));
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
}
