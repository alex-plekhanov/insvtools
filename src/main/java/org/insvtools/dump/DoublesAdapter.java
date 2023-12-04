package org.insvtools.dump;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Adapter to convert doubles array to JSON.
 */
public class DoublesAdapter extends TypeAdapter<double[]> {
    @Override public double[] read(JsonReader jsonReader) {
        // We don't use this method.
        throw new UnsupportedOperationException();
    }

    @Override public void write(JsonWriter jsonWriter, double[] vals) throws IOException {
        jsonWriter.jsonValue(Arrays.toString(vals));
    }
}
