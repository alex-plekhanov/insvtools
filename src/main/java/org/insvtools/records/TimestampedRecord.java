package org.insvtools.records;

public class TimestampedRecord {
    protected long timestamp;

    public TimestampedRecord(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
