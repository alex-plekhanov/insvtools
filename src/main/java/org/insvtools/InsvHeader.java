package org.insvtools;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class InsvHeader {

    /*
     * int64_t First time stamp
     * string offset
     * string the version of FirmWare
     * string camera type
     */
    public static final String SIGNATURE = "8db42d694ccc418790edff439fe026bf";

    public static final int HEADER_SIZE = 72;

    byte[] unknown = new byte[32];
    int version;
    int metaDataSize;
    byte[] signature = new byte[32];

    public InsvHeader(RandomAccessFile file) throws Exception {
        file.seek(file.length() - HEADER_SIZE);

        ByteBuffer buf = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);

        file.read(buf.array());
        buf.get(unknown);
        metaDataSize = buf.getInt();
        version = buf.getInt();
        buf.get(signature);

        if (!Arrays.equals(signature, SIGNATURE.getBytes()))
            throw new Exception("Wrong file signature");

        if (version != 3)
            throw new Exception("Wrong file version " + version);
    }

    public int getVersion() {
        return version;
    }

    public int getMetaDataSize() {
        return metaDataSize;
    }
}
