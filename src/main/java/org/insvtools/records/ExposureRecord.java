package org.insvtools.records;

import java.nio.ByteBuffer;

public class ExposureRecord extends TimestampedRecord {
    public static final int SIZE = TS_SIZE + Double.BYTES;

    private final double shutterSpeed;

    private ExposureRecord(long timestamp, double shutterSpeed) {
        super(timestamp);
        this.shutterSpeed = shutterSpeed;
    }

    public static ExposureRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        double shutterSpeed = buf.getDouble();

        return new ExposureRecord(timestamp, shutterSpeed);
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);

        buf.putDouble(shutterSpeed);
    }

    public double getShutterSpeed() {
        return shutterSpeed;
    }
}
