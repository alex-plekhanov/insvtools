package org.insvtools;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class InsvHeader {
    public static final InsvHeader DUMMY = new InsvHeader(new byte[32], 3, 0, -1);
    public static final String SIGNATURE = "8db42d694ccc418790edff439fe026bf";
    public static final int HEADER_SIZE = 72;
    private final byte[] unknownBuf;
    private final int version;
    private final int metaDataSize;
    private final transient long metaDataPos;

    private InsvHeader(byte[] unknownBuf, int version, int metaDataSize, long metaDataPos) {
        this.unknownBuf = unknownBuf;
        this.version = version;
        this.metaDataSize = metaDataSize;
        this.metaDataPos = metaDataPos;
    }

    /**
     * Read metadata header from the file.
     *
     * @return metadata header or {@code null} if metadata header not found in the file.
     */
    public static @Nullable InsvHeader read(RandomAccessFile file) throws Exception {
        if (file.length() < HEADER_SIZE) {
            return null;
        }

        file.seek(file.length() - HEADER_SIZE);

        ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

        file.readFully(buf.array());

        byte[] unknownBuf = new byte[32];
        buf.get(unknownBuf);
        int metaDataSize = buf.getInt();
        int version = buf.getInt();
        byte[] signature = new byte[32];
        buf.get(signature);

        if (!Arrays.equals(signature, SIGNATURE.getBytes())) {
            return null; // No header.
        }

        if (version != 3) {
            throw new Exception("Unsupported file version " + version);
        }

        return new InsvHeader(unknownBuf, version, metaDataSize, file.length() - metaDataSize);
    }

    public void write(RandomAccessFile file, int size) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
        buf.put(unknownBuf);
        buf.putInt(size);
        buf.putInt(version);
        buf.put(SIGNATURE.getBytes());

        file.write(buf.array());
    }

    public int getVersion() {
        return version;
    }

    public int getMetaDataSize() {
        return metaDataSize;
    }

    public long getMetaDataPos() {
        return metaDataPos;
    }
}
