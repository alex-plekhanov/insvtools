package org.insvtools.frames;

import org.insvtools.InsvMetadata;
import org.insvtools.records.TimestampedRecord;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public abstract class TimestampedFrame extends Frame {
    protected final List<TimestampedRecord> records = new ArrayList<>();

    protected TimestampedFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected boolean parseInternal(InsvMetadata metadata) {
        while (payload.remaining() >= recordSize()) {
            records.add(parseRecord(payload));
        }

        return true;
    }

    protected abstract TimestampedRecord parseRecord(ByteBuffer buffer);

    @Override
    public int writeParsed(RandomAccessFile file) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(recordSize()).order(ByteOrder.LITTLE_ENDIAN);

        for (TimestampedRecord record : records) {
            buf.rewind();
            record.write(buf);
            file.write(buf.array());
        }

        int frameSize = records.size() * recordSize();

        return frameSize + header.write(file, frameSize);
    }

    protected abstract int recordSize();

    public List<TimestampedRecord> getRecords() {
        return records;
    }
}
