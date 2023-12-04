package org.insvtools.commands;

import org.insvtools.InsvMetadata;
import org.insvtools.frames.Frame;

import java.io.File;
import java.io.RandomAccessFile;

public class MetaDecomposeCommand extends AbstractCommand {
    private final int frameType;

    public MetaDecomposeCommand(String fileName, int frameType) {
        super(fileName);
        this.frameType = frameType;
    }

    @Override
    public void run() throws Exception {
        InsvMetadata metadata = readMetaData(fileName);

        String fileName = new File(this.fileName).getName();

        for (int i = 0; i < metadata.getFrames().size(); i++) {
            Frame frame = metadata.getFrames().get(i);

            if (frameType > 0 && frame.getHeader().getFrameTypeCode() != frameType) {
                continue;
            }

            StringBuilder typeBuilder = new StringBuilder().append(frame.getHeader().getFrameTypeCode());

            if (frame.getHeader().getFrameVersion() > 0) {
                typeBuilder.append('_').append(frame.getHeader().getFrameVersion());
            }

            String frameFileName = String.format("%s.frame%02d.type%s.meta", fileName, i, typeBuilder);

            File frameFile = new File(frameFileName);

            if (frameFile.exists()) {
                throw new Exception("File " + frameFileName + " already exists");
            }

            logger.info("Storing frame (typeCode=" + frame.getHeader().getFrameTypeCode() + ",ver=" +
                    frame.getHeader().getFrameVersion()  + ") from " + fileName + " to " + frameFileName);

            try (RandomAccessFile frameFileHandler = new RandomAccessFile(frameFileName, "rw")) {
                frameFileHandler.write(frame.getPayload().array());
            }
        }
    }
}
