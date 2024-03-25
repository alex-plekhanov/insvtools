package org.insvtools.records;

import java.nio.ByteBuffer;

public class GyroV2Record extends TimestampedRecord {
    public static final int SIZE = TS_SIZE + 6 * Short.BYTES;
    private final short payload[];

    private GyroV2Record(long timestamp, short[] payload) {
        super(timestamp);
        this.payload = payload;
    }

    public static GyroV2Record parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        short[] payload = new short[6];

        for (int i = 0; i < payload.length; i++) {
            payload[i] = buf.getShort();
        }

        return new GyroV2Record(timestamp, payload);
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);

        assert payload.length == 6;

        for (short v : payload) {
            buf.putShort(v);
        }
    }

    public short[] getPayload() {
        return payload.clone();
    }
}
