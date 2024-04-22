package org.insvtools.commands;

import org.insvtools.InsvHeader;

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

        try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            InsvHeader metaHeader = InsvHeader.read(file);

            if (metaHeader == null)
                throw new Exception("Metadata not found");

            file.seek(metaHeader.getMetaDataPos());

            logger.info("Extracting metadata from " + fileName + " to " + metaFileName);

            byte[] buf = new byte[8192];

            try (RandomAccessFile metaFileHandler = new RandomAccessFile(metaFile, "rw")) {
                int read;

                while ((read = file.read(buf)) > 0)
                    metaFileHandler.write(buf, 0, read);
            }
        }
    }
}
