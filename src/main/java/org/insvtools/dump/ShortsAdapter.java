package org.insvtools.dump;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Adapter to convert shorts array to JSON.
 */
public class ShortsAdapter extends TypeAdapter<short[]> {
    @Override public short[] read(JsonReader jsonReader) {
        // We don't use this method.
        throw new UnsupportedOperationException();
    }

    @Override public void write(JsonWriter jsonWriter, short[] vals) throws IOException {
        jsonWriter.jsonValue(Arrays.toString(vals));
    }
}
