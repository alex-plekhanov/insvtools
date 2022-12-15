package org.insvtools.logger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerImpl implements Logger {
    private final PrintStream logFile;
    private final Class<?> cls;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LoggerImpl(PrintStream logFile, Class<?> cls) {
        this.logFile = logFile;
        this.cls = cls;
    }

    private String format(String level, String msg) {
        return df.format(new Date()) + " [" + level + "] " + msg;
    }

    @Override public void debug(String msg) {
        logFile.println(format("DEBUG", msg));
        logFile.flush();
    }

    @Override public void info(String msg) {
        System.out.println(msg);

        logFile.println(format("INFO ", msg));
        logFile.flush();
    }

    @Override public void error(String msg, Throwable err) {
        System.err.println(msg);

        logFile.println(format("ERROR", msg));
        err.printStackTrace(logFile);
        logFile.flush();
    }
}
