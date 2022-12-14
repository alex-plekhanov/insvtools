package org.insvtools.records;

import java.nio.ByteBuffer;

public class GpsRecord extends TimestampedRecord {
    private final double latitude;
    private final double longitude;
    private final double altitude;

    public GpsRecord(long timestamp, double latitude, double longitude, double altitude) {
        super(timestamp);
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public static GpsRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();
        double latitude = buf.getDouble();
        double longitude = buf.getDouble();
        double altitude = buf.getDouble();

        return new GpsRecord(timestamp, latitude, longitude, altitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }
}
