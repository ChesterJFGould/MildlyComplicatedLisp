package sexpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NullTest {
	Null n;

	@BeforeEach
	void constructorTest() {
		this.n = new Null();
	}

	@Test
	void toStringTest() {
		assertEquals("()", n.toString());
	}

	@Test
	void typeTest() {
		assertEquals(Type.Null, n.type());
	}

	@Test
	void equalsTest() {
		assertTrue(this.n.equals(n));
		assertTrue(this.n.equals(new Null()));
		assertFalse(this.n.equals(new Float(10)));
	}

	@Test
	void evalTest() {
		assertTrue(n.eval(new Environment()).equals(new Null()));
	}
}
