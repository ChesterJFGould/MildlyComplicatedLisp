package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class SoundTest {
    Sound florence;
    Environment env;

    @BeforeEach
    void setup() throws Exception {
        this.florence = new Sound("data/Florence.wav");
        this.env = new Environment();
    }

    @Test
    void constructorTest() throws Exception {
        assertEquals("data/Florence.wav", this.florence.getPath());
        assertThrows(Exception.class, () -> new Sound("THIS IS NOT A FILE"));
    }

    @Test
    void toStringTest() {
        assertEquals("<Sound data/Florence.wav>", florence.toString());
    }

    @Test
    void typeTest() {
        assertEquals(florence.type(), Type.Sound);
    }

    @Test
    void evalTest() {
        assertEquals(florence.eval(this.env), florence);
    }

    @Test
    void equalityTest() throws Exception {
        assertTrue(florence.equals(florence));
        assertFalse(florence.equals(new Sound("data/Florence.wav")));
        assertFalse(florence.equals(new Null()));
    }

    @Test
    void playPauseTest() throws InterruptedException {
        assertFalse(this.florence.getClip().isRunning());
        this.florence.play();
        Thread.sleep(1000);
        assertTrue(this.florence.getClip().isActive());
        this.florence.pause();
        assertFalse(this.florence.getClip().isRunning());
    }

    @Test
    void toJsonTest() {
        JSONObject fJSON = florence.toJson();
        assertTrue(fJSON.has("type"));
        assertEquals("sound", fJSON.getString("type"));
        assertTrue(fJSON.has("value"));
        assertEquals("data/Florence.wav", fJSON.getString("value"));
    }

    @Test
    void fromJsonTest() throws Exception {
        JSONObject tJSON = florence.toJson();
        assertEquals("<Sound data/Florence.wav>", Sound.fromJson(tJSON).toString());

        assertThrows(Exception.class, () -> Bool.fromJson(new Null().toJson()));
        assertThrows(Exception.class, () -> Bool.fromJson(new JSONObject("{}")));
        assertThrows(Exception.class, () -> Bool.fromJson(new JSONObject("{\"type\":\"sound\"}")));
    }
}
