package org.insvtools.frames;

import org.insvtools.InsvMetadata;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class IndexFrame extends Frame {
    public static final int FRAME_HEADER_SIZE = 1 + 1 + 4 + 4;
    List<FrameHeader> framesIndex = new ArrayList<>();

    protected IndexFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected boolean parseInternal(InsvMetadata metadata) throws Exception {
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

        return true;
    }

    @Override
    public int write(RandomAccessFile file) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(FRAME_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        byte[] emptyHdr = new byte[FRAME_HEADER_SIZE];
        for (FrameHeader hdr : framesIndex) {
            if (hdr == null)
                file.write(emptyHdr);
            else {
                buf.clear();
                buf.put(hdr.getFrameTypeCode());
                buf.put(hdr.getFrameVersion());
                buf.putInt(hdr.getFrameSize());
                buf.putInt((int)hdr.getFramePos());
                file.write(buf.array());
            }
        }

        int frameSize = framesIndex.size() * FRAME_HEADER_SIZE;

        return frameSize + header.write(file, frameSize);
    }

    public List<FrameHeader> getFramesIndex() {
        return framesIndex;
    }
}
