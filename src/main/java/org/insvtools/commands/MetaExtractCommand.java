package org.insvtools.commands;

import org.insvtools.InsvMetadata;

import java.io.File;
import java.io.RandomAccessFile;

public class MetaExtractCommand extends AbstractCommand {
    private final String metaFileName;

    public MetaExtractCommand(String fileName, String metaFileName) {
        super(fileName);

        fileName = new File(fileName).getName();

        this.metaFileName = metaFileName == null ? fileName + ".meta" : metaFileName;
    }

    @Override
    public void run() throws Exception {
        File metaFile = new File(metaFileName);

        if (metaFile.exists()) {
            throw new Exception("File " + metaFileName + " already exists");
        }

        InsvMetadata metadata = readMetaData(fileName);

        logger.info("Extracting metadata from " + fileName + " to " + metaFileName);

        try (RandomAccessFile metaFileHandler = new RandomAccessFile(metaFile, "rw")) {
            metadata.write(metaFileHandler);
        }
    }
}
