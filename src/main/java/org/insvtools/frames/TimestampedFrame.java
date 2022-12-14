package org.insvtools.frames;

import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class TimestampedFrame extends Frame {
    protected final Function<ByteBuffer, TimestampedRecord> recordFactory;

    protected final List<TimestampedRecord> records = new ArrayList<>();

    protected TimestampedFrame(FrameHeader header, ByteBuffer buffer, Function<ByteBuffer, TimestampedRecord> recordFactory) {
        super(header, buffer);
        this.recordFactory = recordFactory;
    }

    @Override
    public void parse() {
        while (buffer.hasRemaining()) {
            records.add(recordFactory.apply(buffer));
        }
    }
}
