package org.insvtools.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.insvtools.frames.ExtraMetadataProtoBuf.ExtraMetadata;

public class Dumper {
    public static Dumper INSTANCE = new Dumper();

    private final Gson gson;

    private Dumper() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(ExtraMetadata.class, new ExtraMetadataAdapter())
                .registerTypeAdapter(byte[].class, new BytesAdapter())
                .registerTypeAdapter(short[].class, new ShortsAdapter())
                .registerTypeAdapter(double[].class, new DoublesAdapter())
                .setPrettyPrinting()
                .create();
    }

    public String dump(Object obj) {
        return gson.toJson(obj);
    }
}
