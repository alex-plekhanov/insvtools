package org.insvtools.frames;

import org.insvtools.records.ExposureRecord;
import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;

public class ExposureFrame extends TimestampedFrame {
    public ExposureFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected int recordSize() {
        return ExposureRecord.SIZE;
    }

    @Override
    protected TimestampedRecord parseRecord(ByteBuffer buffer) {
        return ExposureRecord.parse(buffer);
    }
}
