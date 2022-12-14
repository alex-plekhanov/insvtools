package org.insvtools.frames;

import org.insvtools.records.GyroRecord;

import java.nio.ByteBuffer;

public class GyroFrame extends TimestampedFrame {
    public GyroFrame(FrameHeader header, ByteBuffer buffer) {
        super(header, buffer, GyroRecord::parse);
    }
}
