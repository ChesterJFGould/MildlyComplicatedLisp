package sexpr;

public class IntTest {
	Int ten;
	Int nTen;
	Int zero;
	Environment env;

	@BeforeEach
	void setup() {
		this.ten = new Int(10);
		this.nTen = new Int(-10);
		this.zero = new Int(0);

		this.env = new Environment();
	}

	@Test
	void constructorTest() {
		assertEquals(this.ten.getVal(), 10);
		assertEquals(this.nTen.getVal(), -10);
		assertEquals(this.zero.getVal(), 0);
	}

	@Test
	void toStringTest() {
		assertEquals(this.ten.toString(), "10");
		assertEquals(this.nTen.toString(), "-10");
		assertEquals(this.zero.toString(), "0");
	}

	@Test
	void evalTest() {
		assertEquals(this.ten.eval(this.env), this.ten);
		assertEquals(this.nTen.eval(this.env), this.nTen);
		assertEquals(this.zero.eval(this.env), this.zero);
	}

	@Test
	void typeTest() {
		assertEquals(this.ten.type(), Type.Int);
		assertEquals(this.nTen.type(), Type.Int);
		assertEquals(this.zero.type(), Type.Int);
	}

	@Test
	void equalsTest() {
		assertTrue(this.ten.equals(new Int(10)));
		assertFalse(this.ten.equals(this.nTen));
		assertFalse(this.ten.equals(this.zero));

		assertTrue(this.nTen.equals(new Int(-10)));
		assertFalse(this.nTen.equals(this.zero));
		assertFalse(this.nTen.equals(this.ten));

		assertTrue(this.zero.equals(new Int(0)));
		assertFalse(this.zero.equals(this.ten));
		assertFalse(this.zero.equals(this.nTen));
	}
}
