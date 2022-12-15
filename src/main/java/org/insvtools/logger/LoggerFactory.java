package org.insvtools.logger;

import java.io.*;

public class LoggerFactory {
    private static PrintStream logFile;

    public static Logger getLogger(Class<?> cls) {
        if (logFile == null) {
            try {
                logFile = new PrintStream(new BufferedOutputStream(new FileOutputStream("insvtools.log", true)));
            } catch (IOException e) {
                System.err.println("Can't create log file, all messages will be dumped to System.out");
                logFile = System.out;
            }
        }

        return new LoggerImpl(logFile, cls);
    }
}
