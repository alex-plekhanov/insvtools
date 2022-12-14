package org.insvtools.frames;

import org.insvtools.records.GpsRecord;

import java.nio.ByteBuffer;

public class GpsFrame extends TimestampedFrame {
    public GpsFrame(FrameHeader header, ByteBuffer buffer) {
        super(header, buffer, GpsRecord::parse);
    }
}
