package sexpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnvironmentTest {
	Environment a;
	Environment empty;
	Environment b;

	@BeforeEach
	void setup() {
		this.a = new Environment();
		this.empty = new Environment(a);
		this.b = new Environment(empty);

		this.a.put("a", new Symbol("a"));
		this.b.put("b", new Symbol("b"));
	}

	@Test
	void constructorGetPutTest() {
		assertTrue(this.b.get("a").equals(new Symbol("a")));
		assertTrue(this.b.get("b").equals(new Symbol("b")));
		assertEquals(null, this.empty.get("b"));
	}

	@Test
	void setTest() {
		assertTrue(b.set("a", new Symbol("c")));
		assertTrue(a.get("a").equals(new Symbol("c")));
		assertFalse(empty.set("b", new Null()));
	}
}
