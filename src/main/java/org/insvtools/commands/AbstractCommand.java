package org.insvtools.commands;

import org.insvtools.InsvHeader;
import org.insvtools.InsvMetadata;
import org.insvtools.logger.Logger;
import org.insvtools.logger.LoggerFactory;

import javax.annotation.Nullable;
import java.io.RandomAccessFile;

public abstract class AbstractCommand implements Command {
    protected final String fileName;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AbstractCommand(String fileName) {
        this.fileName = fileName;
    }

    protected static @Nullable InsvMetadata readMetaDataOptional(String fileName) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r")) {
            return InsvMetadata.read(file);
        }
    }

    protected static InsvMetadata readMetaData(String fileName) throws Exception {
        InsvMetadata metadata = readMetaDataOptional(fileName);

        if (metadata == null) {
            throw new Exception("Metadata not found");
        }

        return metadata;
    }

    protected static void replaceMeta(String fileName, InsvMetadata metadata) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            InsvHeader header = InsvHeader.read(file);

            if (header != null) {
                file.setLength(header.getMetaDataPos());
            }

            file.seek(file.length());

            metadata.write(file);
        }
    }
}
