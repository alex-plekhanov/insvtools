package org.insvtools.commands;

import org.insvtools.InsvMetadata;
import org.insvtools.dump.Dumper;
import org.insvtools.frames.Frame;

import java.io.File;
import java.io.PrintWriter;

public class MetaDumpCommand extends AbstractCommand {
    private final int frameType;
    private final String outFileName;

    public MetaDumpCommand(String fileName, int frameType, String outFileName) {
        super(fileName);
        this.frameType = frameType;
        this.outFileName = outFileName;
    }

    @Override
    public void run() throws Exception {
        InsvMetadata metadata = readMetaData(fileName);

        metadata.parse();

        String fileName = new File(this.fileName).getName();

        String outFileName;
        Object dumpObject;

        if (frameType > 0) {
            Frame frame = metadata.findFrame(frameType);

            if (frame == null) {
                throw new Exception("Frame type " + frameType + " not found");
            }

            outFileName = this.outFileName == null ? fileName + ".frame" + frameType + ".meta.json" : this.outFileName;
            dumpObject = frame;
        }
        else {
            outFileName = this.outFileName == null ? fileName + ".meta.json" : this.outFileName;
            dumpObject = metadata;
        }

        if (new File(outFileName).exists()) {
            throw new Exception("File " + outFileName + " already exists");
        }

        logger.info("Dumping metadata to " + outFileName);

        try (PrintWriter out = new PrintWriter(outFileName)) {
            out.println(Dumper.INSTANCE.dump(dumpObject));
        }
    }
}
