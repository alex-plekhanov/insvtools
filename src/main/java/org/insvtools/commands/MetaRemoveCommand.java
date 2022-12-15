package org.insvtools.commands;

import org.insvtools.InsvHeader;

import java.io.RandomAccessFile;

public class MetaRemoveCommand extends AbstractCommand {
    public MetaRemoveCommand(String fileName) {
        super(fileName);
    }

    @Override
    public void run() throws Exception {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            InsvHeader header = InsvHeader.read(file);

            if (header == null) {
                throw new Exception("Metadata not found");
            }

            logger.info("Removing metadata from " + fileName);

            file.setLength(header.getMetaDataPos());
        }
    }
}
