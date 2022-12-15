package org.insvtools.frames;

import org.insvtools.records.GpsRecord;
import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;

public class GpsFrame extends TimestampedFrame {
    public GpsFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected int recordSize() {
        return GpsRecord.SIZE;
    }

    @Override
    protected TimestampedRecord parseRecord(ByteBuffer buffer) {
        return GpsRecord.parse(buffer);
    }
}
