package org.insvtools.commands;

import org.insvtools.InsvHeader;
import org.insvtools.InsvMetadata;
import org.insvtools.frames.Frame;
import org.insvtools.frames.FrameHeader;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaComposeCommand extends AbstractCommand {
    public MetaComposeCommand(String fileName) {
        super(fileName);
    }

    @Override
    public void run() throws Exception {
        String frameFilePrefix = new File(fileName).getName() + ".frame";

        logger.debug("Looking for files with prefix '" + frameFilePrefix + "' in the current directory");

        File[] files = new File(".").listFiles(f -> f.getName().startsWith(frameFilePrefix));

        logger.debug("Found " + (files == null ? null : files.length) + " files");

        if (files == null || files.length == 0)
            throw new Exception("Frame files not found");

        Arrays.sort(files, Comparator.comparing(File::getName));

        List<Frame> frames = new ArrayList<>();

        Pattern typeExtractor = Pattern.compile(".*type(\\d+)(_(\\d+))?\\.meta$");

        for (File frameFile : files) {
            try (RandomAccessFile frameFileHandler = new RandomAccessFile(frameFile, "r")) {
                Matcher matcher = typeExtractor.matcher(frameFile.getName());

                if (!matcher.matches()) {
                    throw new Exception("Wrong frame file name " + frameFile.getName());
                }

                String type = matcher.group(1);
                String ver = matcher.group(3);

                logger.info("Adding frame (type=" + type + ",ver=" + ver  + ") from " + frameFile.getName() +
                        " to " + fileName);

                FrameHeader header = new FrameHeader(
                        Byte.parseByte(type),
                        Byte.parseByte(ver == null ? "0" : ver),
                        (int)frameFile.length(),
                        0
                );
                frames.add(Frame.read(frameFileHandler, header));
            }
        }

        InsvMetadata metadata = new InsvMetadata(InsvHeader.DUMMY, frames);

        replaceMeta(fileName, metadata);
    }
}
