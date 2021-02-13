package sexpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
		assertEquals(a.getVal(), "a");
		assertEquals(b.getVal(), "b");
	}

	@Test
	void evalTest() throws Exception {
		assertTrue(this.a.eval(env).equals(new Int(10)));
		assertTrue(this.b.eval(env).equals(new Int(20)));
		assertThrows(Exception.class, () -> this.c.eval(env));
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
