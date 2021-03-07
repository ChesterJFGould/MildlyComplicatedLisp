package persistence;

import model.Exception;

import java.io.*;

import org.json.*;

public class JsonIO {
    public static void write(JSONObject obj, String fileName) throws Exception {
        String path = "./data/" + fileName + ".slo";
        String json = obj.toString();
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(json);
        } catch (java.lang.Exception e) {
            throw new Exception("cannot write to file %s", path);
        }
    }

    public static JSONObject read(String fileName) throws Exception {
        String path = "./data/" + fileName + ".slo";
        JSONObject out;
        try {
            String json = readFile(path);
            out = new JSONObject(json);
        } catch (java.lang.Exception e) {
            throw new Exception("cannot read JSON from file %s", path);
        }

        return out;
    }

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
