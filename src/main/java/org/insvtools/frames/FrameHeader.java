package org.insvtools.frames;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FrameHeader {
    public static final int FRAME_HEADER_SIZE = 6;

    private final byte frameTypeCode;
    private final FrameType frameType;
    private final byte frameVer;
    private final int frameSize;
    private final transient long framePos;

    public FrameHeader(byte frameTypeCode, byte frameVer, int frameSize, long framePos) {
        this.frameTypeCode = frameTypeCode;
        this.frameType = FrameType.valueOf(frameTypeCode);
        this.frameVer = frameVer;
        this.frameSize = frameSize;
        this.framePos = framePos;
    }

    public FrameHeader(RandomAccessFile file) throws Exception {
        long pos = file.getFilePointer();

        ByteBuffer buf = ByteBuffer.allocate(FRAME_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

        file.seek(pos - FRAME_HEADER_SIZE);

        file.readFully(buf.array());

        frameVer = buf.get();
        frameTypeCode = buf.get();
        frameType = FrameType.valueOf(frameTypeCode);
        frameSize = buf.getInt();
        framePos = pos - frameSize - FRAME_HEADER_SIZE;
    }

    public int write(RandomAccessFile file, int frameSize) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(FRAME_HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

        buf.put(frameVer);
        buf.put(frameTypeCode);
        buf.putInt(frameSize);

        file.write(buf.array());

        return FRAME_HEADER_SIZE;
    }

    public byte getFrameTypeCode() {
        return frameTypeCode;
    }

    public @Nullable FrameType getFrameType() {
        return frameType;
    }

    public byte getFrameVersion() {
        return frameVer;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public long getFramePos() {
        return framePos;
    }

    @Override
    public String toString() {
        return "FrameHeader {" +
            frameType + (frameVer != 0 ? "." + frameVer : "") +
            ", size=" + frameSize +
            ", pos=" + framePos +
            '}';
    }
}
