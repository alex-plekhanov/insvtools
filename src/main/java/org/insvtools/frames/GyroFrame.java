package org.insvtools.frames;

import org.insvtools.InsvMetadata;
import org.insvtools.records.GyroRawRecord;
import org.insvtools.records.GyroRecord;
import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;

public class GyroFrame extends TimestampedFrame {
    private boolean isRaw;
    private int recordSize;

    public GyroFrame(FrameHeader header, ByteBuffer payload) {
        super(header, payload);
    }

    @Override
    protected int recordSize() {
        return recordSize;
    }

    @Override
    protected void parseInternal(InsvMetadata metadata) {
        InfoFrame infoFrame = (InfoFrame)metadata.findFrame(FrameTypes.INFO);

        if (infoFrame == null || !infoFrame.parsed || infoFrame.getExtraMetadata() == null
                || (recordSize = infoFrame.getExtraMetadata().getGyro().size()) == 0) {
            return; // Don't know payload size, can't parse in this case.
        }

        isRaw = recordSize != GyroRecord.SIZE;

        super.parseInternal(metadata);
    }

    @Override
    protected TimestampedRecord parseRecord(ByteBuffer buffer) {
        return isRaw ? GyroRawRecord.parse(buffer, recordSize) : GyroRecord.parse(buffer);
    }
}
