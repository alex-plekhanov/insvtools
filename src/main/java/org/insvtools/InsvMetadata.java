package org.insvtools;

import org.insvtools.frames.Frame;
import org.insvtools.frames.FrameHeader;
import org.insvtools.dump.Dumper;
import org.insvtools.frames.FrameTypes;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsvMetadata {
    private final InsvHeader header;
    private final List<Frame> frames;

    public InsvMetadata(InsvHeader header, List<Frame> frames) {
        this.header = header;
        this.frames = frames;
    }

    /**
     * Read INSV metadata from the file.
     *
     * @return INSV metadata or {@code null} if metadata not found in the file.
     */
    public static @Nullable InsvMetadata read(RandomAccessFile file) throws Exception {
        InsvHeader header = InsvHeader.read(file);

        if (header == null) {
            return null;
        }

        List<Frame> frames = new ArrayList<>();

        long curPos = file.length() - InsvHeader.HEADER_SIZE;

        while (curPos > header.getMetaDataPos()) {
            file.seek(curPos);
            FrameHeader frameHeader = new FrameHeader(file);
            Frame frame = Frame.read(file, frameHeader);
            frames.add(frame);
            curPos = curPos - frameHeader.getFrameSize() - FrameHeader.FRAME_HEADER_SIZE;
        }

        // Since we read frames from last to first, reverse it.
        Collections.reverse(frames);

        return new InsvMetadata(header, frames);
    }

    public void parse() throws Exception {
        Frame infoFrame = findFrame(FrameTypes.INFO);

        // Parse info frame first, since it can be required by other frames.
        if (infoFrame != null) {
            infoFrame.parse(this);
        }

        for (Frame frame : frames) {
            if (frame != infoFrame) {
                frame.parse(this);
            }
        }
    }

    public void write(RandomAccessFile file) throws IOException {
        int size = 0;

        for (Frame frame : frames) {
            size += frame.write(file);
        }

        header.write(file, size + InsvHeader.HEADER_SIZE);
    }

    public InsvHeader getHeader() {
        return header;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public @Nullable Frame findFrame(int frameType) {
        return frames.stream().filter(f -> f.getHeader().getFrameType() == frameType).findFirst().orElse(null);
    }
}
