package org.insvtools;

import org.insvtools.frames.Frame;
import org.insvtools.frames.FrameHeader;
import org.insvtools.frames.FrameType;
import org.insvtools.frames.IndexFrame;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

import static org.insvtools.frames.FrameHeader.FRAME_HEADER_SIZE;

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
        InsvMetadata metadata = new InsvMetadata(header, frames);

        long curPos = file.length() - InsvHeader.HEADER_SIZE;

        while (curPos > header.getMetaDataPos()) {
            file.seek(curPos);
            FrameHeader frameHeader = new FrameHeader(file);
            Frame frame = Frame.read(file, frameHeader);
            frames.add(frame);

            if (frameHeader.getFrameType() == FrameType.INDEX) {
                // Read frames using index frame.
                frame.parse(metadata);

                frames.addAll(readIndexedFrames(file, (IndexFrame)frame));

                break;
            }

            curPos = curPos - frameHeader.getFrameSize() - FRAME_HEADER_SIZE;
        }

        if (frames.size() > 0) {
            Frame lastFrame = frames.get(frames.size() - 1);
            if (lastFrame.getHeader().getFramePos() > header.getMetaDataPos())
                frames.add(Frame.readRaw(file, header.getMetaDataPos(), lastFrame.getHeader().getFramePos()));
        }

        // Since we read frames from last to first, reverse it.
        Collections.reverse(frames);

        return metadata;
    }

    private static Collection<Frame> readIndexedFrames(RandomAccessFile file, IndexFrame indexFrame) throws Exception {
        List<Frame> frames = new ArrayList<>();

        for (FrameHeader frameHeader : indexFrame.getFramesIndex()) {
            if (frameHeader == null)
                continue;

            Frame frame = Frame.read(file, frameHeader);
            frames.add(frame);
        }

        frames.sort(Comparator.comparing(f -> -f.getHeader().getFramePos()));

        List<Frame> res = new ArrayList<>();

        long prevPos = indexFrame.getHeader().getFramePos();

        for (Frame frame : frames) {
            long frameEndPos = frame.getHeader().getFramePos() + frame.getHeader().getFrameSize() + FRAME_HEADER_SIZE;

            if (frameEndPos < prevPos)
                res.add(Frame.readRaw(file, frameEndPos, prevPos));

            res.add(frame);

            prevPos = frame.getHeader().getFramePos();
        }

        return res;
    }

    public void parse() throws Exception {
        Frame infoFrame = findFrame(FrameType.INFO);

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
        if (findFrame(FrameType.INDEX) != null) {
            writeIndexed(file);
            return;
        }

        int size = 0;

        for (Frame frame : frames) {
            size += frame.write(file);
        }

        header.write(file, size + InsvHeader.HEADER_SIZE);
    }

    public void writeIndexed(RandomAccessFile file) throws IOException {
        List<FrameHeader> headers = new ArrayList<>();

        int maxType = 0;

        for (FrameType type : FrameType.values())
            maxType = Math.max(type.getCode(), maxType);

        for (Frame frame : frames)
            maxType = Math.max(frame.getHeader().getFrameTypeCode(), maxType);

        for (int i = 0; i <= maxType; i++)
            headers.add(null);

        IndexFrame indexFrame = null;

        int size = 0;
        long metadataPos = file.getFilePointer();

        for (Frame frame : frames) {
            FrameHeader header = frame.getHeader();

            if (header.getFrameType() == FrameType.INDEX) {
                indexFrame = (IndexFrame)frame;
                continue;
            }

            long pos = file.getFilePointer();
            int frameSize = frame.write(file);

            size += frameSize;

            if (header.getFrameType() == FrameType.RAW)
                continue;

            headers.set(header.getFrameTypeCode(),
                new FrameHeader(header.getFrameTypeCode(), header.getFrameVersion(), frameSize - FRAME_HEADER_SIZE, pos - metadataPos));
        }

        assert indexFrame != null;

        indexFrame.getFramesIndex().clear();
        indexFrame.getFramesIndex().addAll(headers);
        size += indexFrame.write(file);

        header.write(file, size + InsvHeader.HEADER_SIZE);
    }

    public InsvHeader getHeader() {
        return header;
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public @Nullable Frame findFrame(int frameTypeCode) {
        return frames.stream().filter(f -> f.getHeader().getFrameTypeCode() == frameTypeCode).findFirst().orElse(null);
    }

    public @Nullable Frame findFrame(FrameType frameType) {
        return frames.stream().filter(f -> f.getHeader().getFrameType() == frameType).findFirst().orElse(null);
    }

}
