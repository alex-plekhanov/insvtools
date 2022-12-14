package org.insvtools.records;

import java.nio.ByteBuffer;

public class ExposureRecord extends TimestampedRecord {
    private final double shutterSpeed;

    public ExposureRecord(long timestamp, double shutterSpeed) {
        super(timestamp);
        this.shutterSpeed = shutterSpeed;
    }

    public static ExposureRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        double shutterSpeed = buf.getDouble();

        return new ExposureRecord(timestamp, shutterSpeed);
    }

    public double getShutterSpeed() {
        return shutterSpeed;
    }
}
