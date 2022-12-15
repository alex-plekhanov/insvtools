package org.insvtools.records;

import java.nio.ByteBuffer;

public class GyroRawRecord extends TimestampedRecord {
    private final byte[] payload;

    private GyroRawRecord(long timestamp, byte[] payload) {
        super(timestamp);
        this.payload = payload;
    }

    public static GyroRawRecord parse(ByteBuffer buf, int recordSize) {
        long timestamp = buf.getLong();

        byte[] payload = new byte[recordSize - Long.BYTES];
        buf.get(payload);

        return new GyroRawRecord(timestamp, payload);
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);

        buf.put(payload);
    }

    public byte[] getPayload() {
        return payload;
    }
}
