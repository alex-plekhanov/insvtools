package org.insvtools.frames;

import org.insvtools.records.TimelapseRecord;
import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;

/**
 * Frame for sample to timestamp mapping (for timelapse videos).
 */
public class TimelapseFrame extends TimestampedFrame {
    public TimelapseFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected int recordSize() {
        return TimelapseRecord.SIZE;
    }

    @Override
    protected TimestampedRecord parseRecord(ByteBuffer buffer) {
        return TimelapseRecord.parse(buffer);
    }
}
