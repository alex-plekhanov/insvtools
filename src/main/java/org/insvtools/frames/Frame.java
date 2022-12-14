package org.insvtools.frames;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Frame {
    protected final FrameHeader header;
    protected final ByteBuffer buffer;

    protected Frame(FrameHeader header, ByteBuffer buffer) {
        this.header = header;
        this.buffer = buffer;
    }

    public static Frame read(RandomAccessFile file, FrameHeader header) throws IOException {
        file.seek(header.getFramePos());
        byte[] byteBuf = new byte[header.getFrameSize()];
        file.read(byteBuf);

        Frame frame = create(header, ByteBuffer.wrap(byteBuf).order(ByteOrder.LITTLE_ENDIAN));
        frame.parse();

        return frame;
    }

    private static Frame create(FrameHeader header, ByteBuffer buffer) {
        switch (header.getFrameType()) {
            case FrameTypes.INFO:
                return new InfoFrame(header, buffer);
            case FrameTypes.GYRO:
                return new GyroFrame(header, buffer);
            case FrameTypes.EXPOSURE:
                return new ExposureFrame(header, buffer);
            case FrameTypes.GPS:
                return new GpsFrame(header, buffer);
            default:
                return new Frame(header, buffer);
        }
    }

    public void parse() {
    }
}
