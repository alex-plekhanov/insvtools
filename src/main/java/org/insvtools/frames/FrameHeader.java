package org.insvtools.frames;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FrameHeader {
    public static final int FRAME_HEADER_SIZE = 6;

    private byte frameType;
    private byte unknown;
    private int frameSize;
    private long framePos;

    public FrameHeader(RandomAccessFile file) throws Exception {
        long pos = file.getFilePointer();

        ByteBuffer buf = ByteBuffer.allocate(FRAME_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

        file.seek(pos - FRAME_HEADER_SIZE);

        file.read(buf.array());

        unknown = buf.get();
        frameType = buf.get();
        frameSize = buf.getInt();
        framePos = pos - frameSize - FRAME_HEADER_SIZE;
    }

    public byte getFrameType() {
        return frameType;
    }

    public byte getUnknown() {
        return unknown;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public long getFramePos() {
        return framePos;
    }
}
