package model;

import java.io.File;

import org.json.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// Represents a boolean s-expession
public class Sound extends Sexpr {
    private Clip clip;
    private java.lang.String path;

    // MODIFIES: this
    // EFFECT: Initializes a new bool.
    public Sound(java.lang.String path) throws Exception {
        this.path = path;

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());

            this.clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (java.lang.Exception e) {
            throw new Exception("Failed to init Sound with path %s", path);
        }
    }

    // EFFECT: Returns the string representation of this Sound.
    public java.lang.String toString() {
        return "<Sound " + this.path + ">";
    }

    // EFFECTS: Returns this, bools are self-evaluating.
    public Sexpr eval(Environment env) {
        return this;
    }

    // EFFECTS: Returns the Sound Type.
    public Type type() {
        return Type.Sound;
    }

    // EFFECTS: Returns this path.
    public java.lang.String getPath() {
        return this.path;
    }

    // EFFECTS: Returns true if expr the same object
    public boolean equals(Sexpr expr) {
        return expr == this;
    }

    // EFFECTS: Returns the JSON representation of this Sound.
    public JSONObject toJson() {
        return new JSONObject()
                .put("type", "sound")
                .put("value", this.path);
    }

    // EFFECTS: Creates a new bool based on the JSON object. Throws an exception
    // if the obj doesn't represent a Sound.
    public static Sound fromJson(JSONObject obj) throws Exception {
        if (obj.has("type") && obj.getString("type").equals("sound") && obj.has("value")) {
            return new Sound(obj.getString("value"));
        } else {
            throw new Exception("cannot parse Sound from %s", obj);
        }
    }

    // MODIFIES: this clip
    // EFFECT: Play the clip.
    public void play() {
        this.clip.start();
    }

    // MODIFIES: this clip
    // EFFECT: Pause the clip.
    public void pause() {
        this.clip.stop();
    }

    // EFFECT: Returns this clip.
    public Clip getClip() {
        return this.clip;
    }
}
