package persistence;

import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonIOTest {
    @Test
    void ioTest() throws model.Exception {
        JsonIO.write(new Int(10).toJson(), "JsonIOTest");
        assertEquals(new Int(10).toJson().toString(), JsonIO.read("JsonIOTest").toString());

        assertThrows(model.Exception.class, () -> JsonIO.write(new Int(10).toJson(), "thisDirShouldNotExist/test"));
        assertThrows(model.Exception.class, () -> JsonIO.read("thisDirShouldNotExist/test"));
    }
}
