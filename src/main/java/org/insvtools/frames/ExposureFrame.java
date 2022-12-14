package org.insvtools.frames;

import org.insvtools.records.ExposureRecord;

import java.nio.ByteBuffer;

public class ExposureFrame extends TimestampedFrame {
    public ExposureFrame(FrameHeader header, ByteBuffer buffer) {
        super(header, buffer, ExposureRecord::parse);
    }
}
