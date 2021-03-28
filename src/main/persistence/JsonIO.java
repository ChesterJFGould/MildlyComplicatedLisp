package persistence;

import model.Exception;

import java.io.*;

import org.json.*;

// Essentially a namespace around IO operations that act on JSON objects.
public class JsonIO {
    // Writes the given JSON object to the given slo file name in the data directory.
    // Throws an Exception on IOException.
    public static void write(JSONObject obj, String fileName) throws Exception {
        String path = fileName;
        String json = obj.toString();
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(json);
        } catch (java.lang.Exception e) {
            throw new Exception("cannot write to file %s", path);
        }
    }

    // Reads the given slo file name in the data directory into a JSON object.
    // Throws an Exception on IOException.
    public static JSONObject read(String fileName) throws Exception {
        String path = fileName;
        JSONObject out;
        try {
            String json = readFile(path);
            out = new JSONObject(json);
        } catch (java.lang.Exception e) {
            throw new Exception("cannot read JSON from file %s", path);
        }

        return out;
    }

    // Reads the entire contents of the given file path into a String
    // Throws an IOException if the computer or user doesn't like you.
    private static String readFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];

        FileReader file = new FileReader(path);
        int offset = 0;

        while (true) {
            int numRead = file.read(buf, 0, 1024);
            if (numRead < 0) {
                break;
            }

            builder.append(buf, 0, numRead);
        }

        return builder.toString();
    }
}
