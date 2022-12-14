package org.insvtools.records;

import java.nio.ByteBuffer;

public class GyroRecord extends TimestampedRecord {
    private final double[] acceleration;
    private final double[] rotation;

    private GyroRecord(long timstamp, double[] acceleration, double[] rotation) {
        super(timstamp);
        this.acceleration = acceleration;
        this.rotation = rotation;
    }

    public static GyroRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        double[] acceleration = new double[3];
        double[] rotation = new double[3];

        for (int i = 0; i < acceleration.length; i++)
            acceleration[i] = buf.getDouble();

        for (int i = 0; i < rotation.length; i++)
            rotation[i] = buf.getDouble();

        return new GyroRecord(timestamp, acceleration, rotation);
    }

    public double[] getAcceleration() {
        return acceleration.clone();
    }

    public double[] getRotation() {
        return rotation.clone();
    }
}
