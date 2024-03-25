package org.insvtools;

import org.insvtools.commands.*;
import org.insvtools.logger.Logger;
import org.insvtools.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InsvTools {
    private static final Logger logger = LoggerFactory.getLogger(InsvTools.class);

    public static void main(String... args) throws Exception {
        System.exit(run(args));
    }

    public static int run(String... args) throws Exception {
        String ver = InsvTools.class.getPackage().getImplementationVersion();
        if (args.length < 2) {
            System.out.println("InsvTools v." + ver);
            System.out.println("Toolkit for working with Insta360 cameras video files");
            System.out.println("Usage (jar):    java -jar insvtools.jar <cmd> [parameters] <filename>");
            System.out.println("Usage (native): insvtools <cmd> [parameters] <filename>");
            System.out.println("Available commands and parameters:");
            System.out.println("    cut                             - cut the file using start time and/or end time");
            System.out.println("        [--start-time=<time>]       - start time in format [MM:]SS[.SSS]");
            System.out.println("        [--end-time=<time>]         - end time in format [MM:]SS[.SSS]");
            System.out.println("        [--timestamp-scale=<scale>] - Gyro records timestamp scale (autodetect by default)");
            System.out.println("        [--group=<true/false>]      - process the whole group of files related to specified file (true by default)");
            System.out.println("        [--out-file=<filename>]     - use specified output file (by default 'cut' suffix will be added to the original file name)");
            System.out.println();
            System.out.println("    dump-meta                       - dump insv file metadata");
            System.out.println("        [--frame-type=<frame-type>] - dump only specified integer frame type (by default all frames will be dumped)");
            System.out.println("        [--dump-file=<filename>]    - dump file name (by default 'meta.json' suffix will be added to the original file name)");
            System.out.println();
            System.out.println("    decompose-meta                  - store insv file metadata to file-per-frame");
            System.out.println("        [--frame-type=<frame-type>] - store only specified integer frame type (by default all frames will be stored)");
            System.out.println();
            System.out.println("    compose-meta                    - compose file-per-frame metadata to the insv file");
            System.out.println();
            System.out.println("    remove-meta                     - remove insv file metadata");
            System.out.println();
            System.out.println("    extract-meta                    - extract insv file metadata as one file");
            System.out.println("        [--meta-file=<filename>]    - metadata file name (by default 'meta' suffix will be added to the original file name)");
            System.out.println();
            System.out.println("    replace-meta                    - replace insv file metadata with another from file");
            System.out.println("        [--meta-file=<filename>]    - metadata file name (by default 'meta' suffix will be added to the original file name)");
            return 0;
        }

        try {
            Map<String, String> parameters = new HashMap<>();

            String cmdName = args[0];
            String fileName = args[args.length - 1];

            for (int i = 1; i < args.length - 1; i++) {
                if (args[i].contains("=")) {
                    String[] pair = args[i].split("=", 2);
                    parameters.put(pair[0], pair[1]);
                }
                else {
                    throw new Exception("Can't parse parameter: '" + args[i] + "', expected format: --parameter=value");
                }
            }

            Command cmd = null;

            if (cmdName.equals("dump-meta")) {
                String frameType = parameters.remove("--frame-type");
                String dumpFileName = parameters.remove("--dump-file");
                cmd = new MetaDumpCommand(fileName, frameType == null ? 0 : Integer.parseInt(frameType), dumpFileName);
            } else if (cmdName.equals("decompose-meta")) {
                String frameType = parameters.remove("--frame-type");
                cmd = new MetaDecomposeCommand(fileName, frameType == null ? 0 : Integer.parseInt(frameType));
            } else if (cmdName.equals("compose-meta")) {
                cmd = new MetaComposeCommand(fileName);
            } else if (cmdName.equals("remove-meta")) {
                cmd = new MetaRemoveCommand(fileName);
            } else if (cmdName.equals("extract-meta")) {
                String metaFileName = parameters.remove("--meta-file");
                cmd = new MetaExtractCommand(fileName, metaFileName);
            } else if (cmdName.equals("replace-meta")) {
                String metaFileName = parameters.remove("--meta-file");
                cmd = new MetaReplaceCommand(fileName, metaFileName);
            } else if (cmdName.equals("cut")) {
                String startTime = parameters.remove("--start-time");
                String endTime = parameters.remove("--end-time");
                String groupFlag = parameters.remove("--group");
                String cutFileName = parameters.remove("--out-file");
                String timestampScale = parameters.remove("--timestamp-scale");

                boolean groupOfFiles = groupFlag == null || Boolean.parseBoolean(groupFlag);
                long scale = timestampScale == null ? 0 : Long.parseLong(timestampScale);

                if (startTime == null && endTime == null) {
                    throw new Exception("At least one parameter (--start-time or --end-time) should be specified");
                }

                cmd = new CutCommand(fileName, cutFileName, parseTime(startTime), parseTime(endTime), scale, groupOfFiles);
            }

            if (cmd == null) {
                throw new Exception("Unknown command " + cmdName);
            }

            if (!parameters.isEmpty()) {
                throw new Exception("Unknown parameter(s): " + parameters.keySet());
            }

            logger.debug("Command line arguments: " + String.join(" ", args));
            logger.info("InsvTools v." + ver + ", start processing '" + cmdName + "' command");
            cmd.run();
            logger.info("Processed successfully");

            return 0;
        }
        catch (Exception e) {
            logger.error("Failure: " + (e.getMessage() == null ? e : e.getMessage()), e);

            return -1;
        }
    }

    private static double parseTime(String time) throws Exception {
        if (time == null)
            return 0d;

        Pattern pattern = Pattern.compile("((\\d+):)?((\\d+)(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(time);

        if (!matcher.matches()) {
            throw new Exception("Can't parse time " + time);
        }

        String minutes = matcher.group(2);
        String seconds = matcher.group(3);

        return (minutes == null ? 0d : Integer.parseInt(minutes) * 60d) + Double.parseDouble(seconds);
    }
}
