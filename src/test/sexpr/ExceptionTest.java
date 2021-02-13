package sexpr;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {
    @Test
    void constructorTest() {
        Exception e = new Exception("Warning : %s", "Error");
        assertEquals(e.getMessage(), "Warning : Error");
    }
}
