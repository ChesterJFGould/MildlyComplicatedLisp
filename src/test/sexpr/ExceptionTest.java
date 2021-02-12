package sexpr;

public class ExceptionTest {
	@Test
	void constructorTest() {
		Exception e = new Exception("Warning : %s", "Error");
		assertEquals(e.getMessage(), "Warning : Error");
	}
}
