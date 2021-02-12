package sexpr;

public class PairTest {
	Pair base;
	Pair oneTwo;
	Pair oneTwoThree;
	Pair addOneTwo;
	Environment env;

	@BeforeEach
	void setup() {
		this.base = new Pair();
		this.oneTwo = new Pair(new Int(1), new Int(2));
		this.oneTwoThree = new Pair(new Int(1), new Pair(new Int(2),
			new Pair(new Int(3), new Null())));
		this.addOneTwo = new Pair(new Symbol("+"), new Pair(new Int(1),
			new Pair(new Int(2), new Null())));

		this.env = new Environment();

		env.put("+", Procedure.newNumericBinaryOperator(
			(Long a, Long b) -> new Int(a + b),
			(Double a, Double b) -> new Float(a + b)));
	}

	@Test
	void constructorTest() {
		assertEquals(null, this.base.getCar());
		assertEquals(null, this.base.getCdr());

		assertEquals(new Int(1), this.oneTwo.getCar());
		assertEquals(new Int(2), this.oneTwo.getCdr());
	}

	@Test
	void setTest() {
		oneTwo.setCar(new Int(3));
		oneTwo.setCdr(new Int(4));

		assertEquals(new Int(3), oneTwo.getCar());
		assertEquals(new Int(4), oneTwo.getCdr());
	}

	@Test
	void toStringTest() {
		assertEquals(oneTwo.toString(), "(1 . 2)");
		assertEquals("(1 2 3)", oneTwoThree.toString());
	}

	@Test
	void evalTest() {
		assertEquals(new Int(3), addOneTwo.eval(env));
		assertThrows(Exception.class, oneTwoThree.eval(env));
	}

	@Test
	void typeTest() {
		assertEquals(Type.Pair, base.type());
		assertEquals(Type.Pair, oneTwo.type());
	}

	@Test
	void equals() {
		assertTrue(this.base.equals(this.base));
		assertFalse(this.base.equals(new Pair()));

		assertTrue(this.oneTwo.equals(this.oneTwo));
		assertTrue(this.oneTwo.equals(new Pair(new Int(1), new Int(2))));
	}
}
