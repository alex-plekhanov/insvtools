package org.insvtools;

import org.insvtools.frames.Frame;
import org.insvtools.frames.FrameHeader;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class InsvMetadata {
    private final InsvHeader header;
    private final List<Frame> frames;

    private InsvMetadata(InsvHeader header, List<Frame> frames) {
        this.header = header;
        this.frames = frames;
    }

    public static InsvMetadata read(RandomAccessFile file) throws Exception {
        InsvHeader header = new InsvHeader(file);

        List<Frame> frames = new ArrayList<>();

        long startPos = file.length() - header.getMetaDataSize();
        long curPos = file.length() - InsvHeader.HEADER_SIZE;

        while (curPos > startPos) {
            file.seek(curPos);
            FrameHeader frameHeader = new FrameHeader(file);
            Frame frame = Frame.read(file, frameHeader);
            frames.add(frame);
            curPos = curPos - frameHeader.getFrameSize() - FrameHeader.FRAME_HEADER_SIZE;
        }

        return new InsvMetadata(header, frames);
    }

    public InsvHeader getHeader() {
        return header;
    }

    public List<Frame> getFrames() {
        return frames;
    }
}
