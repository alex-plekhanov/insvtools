package org.insvtools.dump;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.protobuf.util.JsonFormat;
import org.insvtools.frames.ExtraMetadataProtoBuf.ExtraMetadata;

import java.io.IOException;

/**
 * Adapter to convert protobuf message ExtraMetadata to JSON.
 */
public class ExtraMetadataAdapter extends TypeAdapter<ExtraMetadata> {
    @Override
    public ExtraMetadata read(JsonReader jsonReader) {
        // We don't use this method.
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(JsonWriter jsonWriter, ExtraMetadata extraMetadata) throws IOException {
        String indent = "        ";
        jsonWriter.jsonValue(System.lineSeparator() +
            JsonFormat.printer()
                .print(extraMetadata)
                .replaceAll("(?m)^", indent)
        );
    }
}
