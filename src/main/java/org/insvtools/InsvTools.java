package org.insvtools;

import org.insvtools.frames.FrameHeader;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class InsvTools {
    public static final String INSV_EXTENSION = ".insv";

    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new Exception("Usage: insvtools filename");

        parseMetaData(args[0]);
    }

    private static void parseMetaData(String fileName) throws Exception {
        RandomAccessFile file = new RandomAccessFile(fileName, "r");
        InsvHeader header = new InsvHeader(file);
        List<FrameHeader> frameHeaders = new ArrayList<>();

        long startPos = file.length() - header.getMetaDataSize();

        long curPos = file.length() - InsvHeader.HEADER_SIZE;

        while (curPos > startPos) {
            file.seek(curPos);
            FrameHeader frameHeader = new FrameHeader(file);
            frameHeaders.add(frameHeader);
            curPos = curPos - frameHeader.getFrameSize() - FrameHeader.FRAME_HEADER_SIZE;
        }

        String fileNamePrefix = fileName.toLowerCase().endsWith(INSV_EXTENSION.toLowerCase()) ?
            fileName.substring(0, fileName.length() - INSV_EXTENSION.length()) :
            fileName;

        for (FrameHeader frameHeader : frameHeaders) {
            String frameFileName = fileNamePrefix + "_frame" + frameHeader.getFrameType() + ".bin";

            RandomAccessFile frameFile = new RandomAccessFile(frameFileName, "rw");

            file.seek(frameHeader.getFramePos());
            byte[] buf = new byte[frameHeader.getFrameSize()];
            file.read(buf);
            frameFile.write(buf);
            frameFile.close();
        }

        file.close();
    }
}
