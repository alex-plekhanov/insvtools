package org.insvtools.commands;

import org.insvtools.InsvMetadata;

import java.io.File;

public class MetaReplaceCommand extends AbstractCommand {
    private final String metaFileName;

    public MetaReplaceCommand(String fileName, String metaFileName) {
        super(fileName);

        fileName = new File(fileName).getName();

        this.metaFileName = metaFileName == null ? fileName + ".meta" : metaFileName;
    }

    @Override
    public void run() throws Exception {
        InsvMetadata metadata = readMetaData(metaFileName);

        logger.info("Replacing metadata from " + fileName + " to " + metaFileName);

        replaceMeta(fileName, metadata);
    }
}
