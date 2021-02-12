package sexpr;

public class SymbolTest {
	Symbol a;
	Symbol b;
	Symbol c;
	Environment env;

	@BeforeEach
	void setup() {
		this.a = new Symbol("a");
		this.b = new Symbol("b");
		this.c = new Symbol("c");

		this.env = new Environment();
		env.put("a", new Int(10));
		env.put("b", new Int(20));
	}

	@Test
	void constructorTest() {
		assertEquals(symbol.getVal(), "symbol");
	}

	@Test
	void evalTest() {
		assertEquals(this.a.eval(env), new Int(10));
		assertEquals(this.b.eval(env), new Int(20));
		assertThrows(this.c.eval(env), Exception.class);
	}

	@Test
	void typeTest() {
		assertEquals(Type.Symbol, this.a.type());
		assertEquals(Type.Symbol, this.c.type());
	}

	@Test
	void equalsTest() {
		assertTrue(this.a.equals(new Symbol("a")));
		assertFalse(this.a.equals(this.b));
		assertFalse(this.b.equals(new Null()));
	}
}
