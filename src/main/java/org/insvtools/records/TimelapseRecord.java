package org.insvtools.records;

import java.nio.ByteBuffer;

/**
 * Sample to timestamp mapping record (for timelapse videos).
 */
public class TimelapseRecord extends TimestampedRecord {
    public static final int SIZE = TS_SIZE;

    private TimelapseRecord(long timestamp) {
        super(timestamp);
    }

    public static TimelapseRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();

        return new TimelapseRecord(timestamp);
    }
}
