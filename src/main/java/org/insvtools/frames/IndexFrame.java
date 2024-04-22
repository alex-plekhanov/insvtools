package org.insvtools.frames;

import org.insvtools.InsvMetadata;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class IndexFrame extends Frame {
    public static final int FRAME_HEADER_SIZE = 1 + 1 + 4 + 4;
    List<FrameHeader> framesIndex = new ArrayList<>();

    protected IndexFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected void parseInternal(InsvMetadata metadata) throws Exception {
        if (payload.remaining() % FRAME_HEADER_SIZE != 0)
            throw new Exception("Unexpected INDEX frame size: " + payload.remaining());

        while (payload.remaining() > 0) {
            byte type = payload.get();
            byte version = payload.get();
            int size = payload.getInt();
            long offset = metadata.getHeader().getMetaDataPos() + payload.getInt();

            if (type != 0 || version != 0 || size != 0)
                framesIndex.add(new FrameHeader(type, version, size, offset));
            else
                framesIndex.add(null);
        }
    }

    public List<FrameHeader> getFramesIndex() {
        return framesIndex;
    }
}
