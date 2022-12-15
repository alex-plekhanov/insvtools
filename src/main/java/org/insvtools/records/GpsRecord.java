package org.insvtools.records;

import java.nio.ByteBuffer;

public class GpsRecord extends TimestampedRecord {
    public static final int SIZE = TS_SIZE + 45;
    private final byte[] payload;
    private final String description;

    private GpsRecord(long timestamp, byte[] payload, String description) {
        super(timestamp);
        this.payload = payload;
        this.description = description;
    }

    public static GpsRecord parse(ByteBuffer buf) {
        long timestamp = buf.getLong();

        int pos = buf.position();
        byte[] payload = new byte[SIZE - TS_SIZE];
        buf.get(payload);

        buf.position(pos);

        buf.getShort(); // Skip unknown.
        buf.get(); // Skip unknown.

        double latitude = buf.getDouble();
        char ns = (char)buf.get();
        double longitude = buf.getDouble();
        char ew = (char)buf.get();
        double speed = buf.getDouble();
        double track = buf.getDouble();
        double altitude = buf.getDouble();

        String description = "position " + latitude + ns + ' ' + longitude + ew +
                " speed " + speed +
                " track " + track +
                " altitude " + altitude;

        return new GpsRecord(timestamp, payload, description);
    }

    @Override
    public void write(ByteBuffer buf) {
        super.write(buf);
        buf.put(payload);
    }

    public String getDescription() {
        return description;
    }
}
