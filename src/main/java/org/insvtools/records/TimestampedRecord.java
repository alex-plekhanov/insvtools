package org.insvtools.records;

import java.nio.ByteBuffer;

public abstract class TimestampedRecord {
    protected static final int TS_SIZE = Long.BYTES;

    protected long timestamp;

    protected TimestampedRecord(long timestamp) {
        this.timestamp = timestamp;
    }

    public void write(ByteBuffer buf) {
        buf.putLong(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
