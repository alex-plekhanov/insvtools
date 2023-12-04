package org.insvtools.frames;

import org.insvtools.InsvMetadata;
import org.insvtools.records.GyroRawRecord;
import org.insvtools.records.GyroV1Record;
import org.insvtools.records.GyroV2Record;
import org.insvtools.records.TimestampedRecord;

import java.nio.ByteBuffer;

public class GyroFrame extends TimestampedFrame {
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
        InfoFrame infoFrame = (InfoFrame)metadata.findFrame(FrameType.INFO);

        if (infoFrame == null || !infoFrame.parsed || infoFrame.getExtraMetadata() == null
                || (recordSize = infoFrame.getExtraMetadata().getGyro().size()) == 0) {
            return; // Don't know payload size, can't parse in this case.
        }

        super.parseInternal(metadata);
    }

    @Override
    protected TimestampedRecord parseRecord(ByteBuffer buffer) {
        if (recordSize == GyroV1Record.SIZE)
            return GyroV1Record.parse(buffer);
        else if (recordSize == GyroV2Record.SIZE)
            return GyroV2Record.parse(buffer);
        else
            return GyroRawRecord.parse(buffer, recordSize);
    }
}
