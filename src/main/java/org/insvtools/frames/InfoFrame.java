package org.insvtools.frames;

import com.google.protobuf.ByteString;
import org.insvtools.InsvMetadata;
import org.insvtools.records.GyroRawRecord;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InfoFrame extends Frame {
    private ExtraMetadataProtoBuf.ExtraMetadata extraMetadata;
    private GyroRawRecord gyroRecord;

    private transient ExtraMetadataProtoBuf.ExtraMetadata.Builder extraMetadataBuilder;

    public InfoFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected boolean parseInternal(InsvMetadata metadata) throws Exception {
        // Version == 1 : ProtoBuf serializer
        // Version != 1 : JSON serializer
        if (header.getFrameVersion() != 1) {
            throw new Exception("Unsupported InfoFrame version " + header.getFrameVersion());
        }

        extraMetadata = ExtraMetadataProtoBuf.ExtraMetadata.parseFrom(payload);
        int gyroDataSize = extraMetadata.getGyro().size();
        ByteBuffer gyroDataBuf = extraMetadata.getGyro().asReadOnlyByteBuffer().order(ByteOrder.LITTLE_ENDIAN);
        gyroRecord = gyroDataSize == 0 ? null : GyroRawRecord.parse(gyroDataBuf, gyroDataSize);

        return true;
    }

    @Override
    public int writeParsed(RandomAccessFile file) throws IOException {
        flushBuilder();

        byte[] buf = extraMetadata.toByteArray();
        file.write(buf);
        return buf.length + header.write(file, buf.length);
    }

    public ExtraMetadataProtoBuf.ExtraMetadata getExtraMetadata() {
        assert parsed : "Metadata is not parsed";

        flushBuilder();

        return extraMetadata;
    }

    public void setFileSize(long fileSize) {
        createBuilder();

        extraMetadataBuilder.setFileSize(fileSize);
    }

    public void setFirstFrameTimestamp(long timestamp) {
        createBuilder();

        extraMetadataBuilder.setFirstFrameTimestamp(timestamp);
    }

    public void setTotalTime(int totalTime) {
        createBuilder();

        extraMetadataBuilder.setTotalTime(totalTime);
    }

    public long getGyroTimestamp() {
        return gyroRecord == null ? -1 : gyroRecord.getTimestamp();
    }

    public void setGyroTimestamp(long timestamp) {
        if (gyroRecord == null) {
            return;
        }

        createBuilder();

        ByteBuffer buf = ByteBuffer.allocate(extraMetadataBuilder.getGyro().size()).order(ByteOrder.LITTLE_ENDIAN);

        gyroRecord.setTimestamp(timestamp);
        gyroRecord.write(buf);
        buf.rewind();

        extraMetadataBuilder.setGyro(ByteString.copyFrom(buf));
    }

    public void setFirstGpsTimestamp(long timestamp) {
        createBuilder();

        extraMetadataBuilder.setFirstGpsTimestamp(timestamp);
    }

    private void flushBuilder() {
        if (extraMetadataBuilder != null) {
            extraMetadata = extraMetadataBuilder.build();
            extraMetadataBuilder = null;
        }
    }

    private void createBuilder() {
        assert parsed : "Metadata is not parsed";

        if (extraMetadataBuilder == null) {
            extraMetadataBuilder = extraMetadata.toBuilder();
        }
    }
}
