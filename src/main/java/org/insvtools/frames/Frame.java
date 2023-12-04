package org.insvtools.frames;

import org.insvtools.InsvMetadata;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Frame {
    protected final FrameHeader header;
    protected final transient ByteBuffer payload;
    protected transient boolean parsed;

    protected Frame(FrameHeader header, ByteBuffer payload) {
        this.header = header;
        this.payload = payload;
    }

    public static Frame read(RandomAccessFile file, FrameHeader header) throws Exception {
        file.seek(header.getFramePos());
        byte[] byteBuf = new byte[header.getFrameSize()];
        file.readFully(byteBuf);

        ByteBuffer byteBuffer = ByteBuffer.wrap(byteBuf).order(ByteOrder.LITTLE_ENDIAN);

        Frame frame;

        frame = create(header, byteBuffer);

        byteBuffer.rewind();

        return frame;
    }

    public int write(RandomAccessFile file) throws IOException {
        return parsed ? writeParsed(file) : writePayload(file);
    }

    protected int writeParsed(RandomAccessFile file) throws IOException {
        return writePayload(file);
    }

    private int writePayload(RandomAccessFile file) throws IOException {
        file.write(payload.array());

        return payload.array().length + header.write(file, payload.array().length);
    }

    private static Frame create(FrameHeader header, ByteBuffer buffer) {
        switch (header.getFrameType()) {
            case INFO:
                return new InfoFrame(header, buffer);
            case GYRO:
                return new GyroFrame(header, buffer);
            case EXPOSURE:
                return new ExposureFrame(header, buffer);
            case TIMELAPSE:
                return new TimelapseFrame(header, buffer);
            case GPS:
                return new GpsFrame(header, buffer);
            default:
                return new Frame(header, buffer);
        }
    }

    public void parse(InsvMetadata metadata) throws Exception {
        if (parsed) {
            return;
        }

        parseInternal(metadata);
        parsed = true;
    }

    protected void parseInternal(InsvMetadata metadata) throws Exception {
        // By default, do nothing with payload
    }

    public FrameHeader getHeader() {
        return header;
    }

    public ByteBuffer getPayload() {
        return payload;
    }
}
