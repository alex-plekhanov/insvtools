package org.insvtools.records;

import java.nio.ByteBuffer;

public class GyroRecord extends TimestampedRecord {
    public static final int SIZE = TS_SIZE + 6 * Double.BYTES;
    private final double payload[];

    private GyroRecord(long timestamp, double[] payload) {
        super(timestamp);
        this.payload = payload;
    }

    public static GyroRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        double[] payload = new double[6];

        for (int i = 0; i < payload.length; i++) {
            payload[i] = buf.getDouble();
        }

        // Not sure about the data structure, just use abstract payload.
/*
        double[] acceleration = new double[3];
        double[] rotation = new double[3];

        for (int i = 0; i < acceleration.length; i++) {
            acceleration[i] = buf.getDouble();
        }

        for (int i = 0; i < rotation.length; i++) {
            rotation[i] = buf.getDouble();
        }
*/

        return new GyroRecord(timestamp, payload);
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);

        assert payload.length == 6;

        for (double v : payload) {
            buf.putDouble(v);
        }
    }

    public double[] getPayload() {
        return payload.clone();
    }
}
