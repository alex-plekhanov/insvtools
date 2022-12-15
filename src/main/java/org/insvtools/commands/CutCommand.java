package org.insvtools.commands;

import org.insvtools.InsvHeader;
import org.insvtools.InsvMetadata;
import org.insvtools.frames.FrameTypes;
import org.insvtools.frames.InfoFrame;
import org.insvtools.frames.TimelapseFrame;
import org.insvtools.records.TimestampedRecord;
import org.mp4parser.Container;
import org.mp4parser.muxer.FileRandomAccessSourceImpl;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.Track;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.container.mp4.MovieCreator;
import org.mp4parser.muxer.tracks.AppendTrack;
import org.mp4parser.muxer.tracks.ClippedTrack;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CutCommand extends AbstractCommand {
    private static final String INSV_EXTENSION = ".insv";
    private static final String VIDEO_HANDLER_TYPE = "vide";
    private final File mainFile; // Main file in group (file name was explicitly provided)
    private final String cutFileName; // Cut file name for main file (can be null)
    private final double startTime;
    private final double endTime;
    private final boolean groupOfFiles; // Process the whole group of files related to passed file.

    public CutCommand(String fileName, String cutFileName, double startTime, double endTime, boolean groupOfFiles) {
        super(fileName);
        this.mainFile = new File(fileName);
        this.cutFileName = cutFileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groupOfFiles = groupOfFiles;
    }

    private File cutFileFor(File file) {
        if (file.getName().equals(mainFile.getName()) && cutFileName != null) {
            return new File(cutFileName);
        }

        String mainFileNameWoExt = mainFile.getName().replaceAll(INSV_EXTENSION + "$", "");
        String fileNameWOExt = file.getName().replaceAll(INSV_EXTENSION + "$", "");

        if (cutFileName == null) {
            return new File(fileNameWOExt + ".cut" + INSV_EXTENSION);
        }
        else if (!cutFileName.contains(mainFileNameWoExt)) {
            File cutDir = new File(cutFileName).getParentFile();

            return new File(cutDir, fileNameWOExt + ".cut" + INSV_EXTENSION);
        }
        else {
            return new File(cutFileName.replace(mainFileNameWoExt, fileNameWOExt));
        }
    }

    /**
     * Create map of files to process (input) to cut (output) files.
     */
    private Map<File, File> filesToProcess() {
        Pattern insvFilePattern = Pattern.compile("(VID|LRV)_(\\d{8}_\\d{6})_\\d\\d_(\\d+\\.insv)");

        Matcher matcher = insvFilePattern.matcher(mainFile.getName());

        if (!groupOfFiles || !matcher.matches()) {
            return Collections.singletonMap(mainFile, cutFileFor(mainFile));
        }

        String dateTime = matcher.group(2);
        String numExt = matcher.group(3);

        File dir = new File(fileName).getAbsoluteFile().getParentFile();

        Pattern groupPattern = Pattern.compile("(VID|LRV)_" + dateTime + "_\\d\\d_" + numExt);

        File[] files = dir.listFiles(f -> groupPattern.matcher(f.getName()).matches());

        assert files != null; // At least fileName should be found.

        return Arrays.stream(files).collect(Collectors.toMap(Function.identity(), this::cutFileFor));
    }

    @Override
    public void run() throws Exception {
        if (!mainFile.exists()) {
            throw new Exception("File " + fileName + " not found");
        }

        Map<File, File> filesToProcess = filesToProcess();

        for (File cutFile : filesToProcess.values()) {
            if (cutFile.exists()) {
                throw new Exception("File " + cutFile.getName() + " already exists");
            }
        }

        for (Map.Entry<File, File> pair : filesToProcess.entrySet()) {
            logger.info("Processing " + pair.getKey().getName() + " -> " + pair.getValue().getName());

            String fileName = pair.getKey().getAbsolutePath();
            File cutFile = pair.getValue();

            double startTimeAdjusted = startTime;
            boolean timeAdjusted = false;

            try (InsvMovie insvMovie = new InsvMovie(pair.getKey())) {
                Movie movie = insvMovie.getMovie();

                List<Track> oldTracks = movie.getTracks();
                movie.setTracks(new ArrayList<>(oldTracks.size()));

                // Here we try to find a track that has sync samples. Since we can only start decoding
                // at such a sample we should make sure that the start of the new fragment is exactly
                // such a frame.
                for (Track track : oldTracks) {
                    if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                        double newStartTime = adjustTimeToSyncSample(track, startTimeAdjusted);

                        if (timeAdjusted) {
                            if (newStartTime != startTimeAdjusted) {
                                throw new Exception("The start time has already been adjusted by another track " +
                                        "with sync samples or another video file. All files should have the same " +
                                        "point of cut (synced samples should have intersection in this point).");
                            }
                        }
                        else {
                            if (endTime > 0 && newStartTime > endTime) {
                                throw new Exception("End time is more than adjusted start time");
                            }
                            logger.debug("Start time has been adjusted by sync samples to new value: " + newStartTime);
                            startTimeAdjusted = newStartTime;
                            timeAdjusted = true;
                        }
                    }
                }

                int videoStartSample = -1;
                int videoEndSample = -1;
                double videoStartTime = -1d;
                double videoEndTime = -1d;

                for (Track track : oldTracks) {
                    double currentTime = 0d;
                    double firstSampleTime = -1d;
                    double lastSampleTime = -1d;
                    int firstSample = -1;
                    int lastSample = -1;

                    for (int currentSample = 0; currentSample < track.getSampleDurations().length; currentSample++) {
                        long delta = track.getSampleDurations()[currentSample];

                        if (currentTime <= startTimeAdjusted) {
                            firstSampleTime = currentTime;
                            firstSample = currentSample;
                        }

                        if (endTime > 0 && currentTime >= endTime) {
                            lastSample = currentSample;
                            lastSampleTime = currentTime;
                            break;
                        }

                        currentTime += (double)delta / (double)track.getTrackMetaData().getTimescale();
                    }

                    if (firstSample < 0) {
                        throw new Exception("Can't find first sample for time " + startTimeAdjusted);
                    }

                    if (lastSample < 0) {
                        lastSample = track.getSampleDurations().length;
                        lastSampleTime = currentTime;
                    }

                    movie.addTrack(new AppendTrack(new ClippedTrack(track, firstSample, lastSample)));

                    logger.debug("Track " + track.getName() + '(' + track.getHandler() + ") has been clipped [firstSample=" + firstSample + ", lastSample=" + lastSample + ']');

                    if (VIDEO_HANDLER_TYPE.equals(track.getHandler())) {
                        videoStartSample = firstSample;
                        videoStartTime = firstSampleTime;
                        videoEndSample = lastSample;
                        videoEndTime = lastSampleTime;
                    }
                }

                Container out = new DefaultMp4Builder().build(movie);

                InsvMetadata metadata = readMetaDataOptional(fileName);

                if (metadata != null) {
                    metadata.parse();
                    logger.debug("Found INSV metadata [framesCount=" + metadata.getFrames().size() + ']');
                }

                try (RandomAccessFile cutFileHandler = new RandomAccessFile(cutFile, "rw")) {
                    try (FileChannel cutFileChannel = cutFileHandler.getChannel()) {
                        out.writeContainer(cutFileChannel);

                        if (metadata != null) {
                            InfoFrame infoFrame = (InfoFrame)metadata.findFrame(FrameTypes.INFO);

                            assert infoFrame != null;

                            // After version 2 of firmware for some reason scale of timestamps are changed from
                            // millis to micros. I didn't find exact flag or field in metadata to calculate scale,
                            // so decided to rely on version.
                            long scale = infoFrame.getExtraMetadata().getFwVersion().startsWith("v1.") ?
                                    1_000L : 1_000_000L;
                            long firstFrameTs = infoFrame.getExtraMetadata().getFirstFrameTimestamp();
                            long firstGpsTs = infoFrame.getExtraMetadata().getFirstGpsTimestamp();

                            infoFrame.setFileSize(cutFileHandler.getFilePointer());
                            infoFrame.setFirstFrameTimestamp(firstFrameTs + (long)(startTimeAdjusted * scale));
                            infoFrame.setTotalTime((int)Math.round(videoEndTime - videoStartTime));
                            infoFrame.setFirstGpsTimestamp(firstGpsTs + (long)(startTimeAdjusted * scale));

                            TimelapseFrame timelapseFrame = (TimelapseFrame)metadata.findFrame(FrameTypes.TIMELAPSE);

                            // Delete timelapse records for deleted samples.
                            if (timelapseFrame != null) {
                                List<TimestampedRecord> recordsCopy = new ArrayList<>(
                                        timelapseFrame.getRecords().subList(videoStartSample, videoEndSample));

                                timelapseFrame.getRecords().clear();
                                timelapseFrame.getRecords().addAll(recordsCopy);
                            }

                            metadata.write(cutFileHandler);
                        }
                    }
                }
            }
        }
    }

    private static double adjustTimeToSyncSample(Track track, double time) throws Exception {
        int currentSample = 0;
        double currentTime = 0d;
        double previousSyncTime = 0d;
        double timescale = (double)track.getTrackMetaData().getTimescale();

        for (long currentSyncSample : track.getSyncSamples()) {
            // Process samples up to current sync sample.
            while (currentSample < currentSyncSample - 1) { // Sync samples references start with 1.
                currentSample++;
                currentTime += (double)track.getSampleDurations()[currentSample] / timescale;
            }

            if (currentTime > time) {
                return previousSyncTime;
            }

            previousSyncTime = currentTime;
        }

        while (currentSample < track.getSampleDurations().length) {
            currentSample++;
            currentTime += (double)track.getSampleDurations()[currentSample] / timescale;
        }

        if (currentTime >= time) {
            throw new Exception("Start time is more than track length");
        }

        return previousSyncTime;
    }

    /**
     * Wrapper over Movie to limit file stream (cut off INSV metadata) and properly close file after using.
     */
    private static class InsvMovie implements Closeable {
        private final Movie movie;
        private final RandomAccessFile file;

        public InsvMovie(File file) throws Exception {
            this.file = new RandomAccessFile(file, "r");

            try {
                InsvHeader insvHeader = InsvHeader.read(this.file);
                this.file.seek(0);

                try (FileInputStream fis = new FileInputStream(file)) {
                    ReadableByteChannel fc = (insvHeader != null) ?
                            new LimitedReadableChannel(fis.getChannel(), insvHeader.getMetaDataPos()) :
                            fis.getChannel();

                    movie = MovieCreator.build(fc, new FileRandomAccessSourceImpl(this.file), file.getName());
                }
            }
            catch (Exception e) {
                this.file.close();

                throw e;
            }
        }

        public Movie getMovie() {
            return movie;
        }

        @Override
        public void close() throws IOException {
            file.close();
        }
    }

    /**
     * ReadableByteChannel with ability to limit count of bytes to read.
     */
    private static class LimitedReadableChannel implements ReadableByteChannel {
        private final FileChannel delegate;
        private final long limit;
        private long read;

        private LimitedReadableChannel(FileChannel delegate, long limit) {
            this.delegate = delegate;
            this.limit = limit;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (read == limit) {
                return -1;
            }

            int oldLimit = dst.limit();

            try {
                if (read + dst.remaining() > limit) {
                    dst.limit((int)(limit - read - dst.position()));
                }

                int read = delegate.read(dst);
                this.read += read;

                return read;
            }
            finally {
                dst.limit(oldLimit);
            }
        }

        @Override
        public boolean isOpen() {
            return delegate.isOpen();
        }

        @Override
        public void close() throws IOException {
            delegate.close();
        }
    }
}
