package org.insvtools.dump;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Adapter to convert bytes array to JSON.
 */
public class BytesAdapter extends TypeAdapter<byte[]> {
    @Override public byte[] read(JsonReader jsonReader) {
        // We don't use this method.
        throw new UnsupportedOperationException();
    }

    @Override public void write(JsonWriter jsonWriter, byte[] bytes) throws IOException {
        StringBuilder sb = new StringBuilder(bytes.length * 4 + 2);

        sb.append('"');

        for (byte b : bytes) {
            sb.append(String.format("\\x%02X", b));
        }

        sb.append('"');

        jsonWriter.jsonValue(sb.toString());
    }
}
