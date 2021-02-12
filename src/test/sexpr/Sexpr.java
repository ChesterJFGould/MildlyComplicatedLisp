package sexpr;

public class SexprTest {
	@Test
	void readTest() {
		CharStream cs = new CharStream(new StringBufferInputStream("(1 \"two\" three . 4.0)"));
		assertEquals(new Pair(new Int(1),
		                      new Pair(new String("two"),
		                               new Pair(new Symbol("three"),
		                                        new Pair(new Float(4), new Null())))),
			Sexpr.read(cs));
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
