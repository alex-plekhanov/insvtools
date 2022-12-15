package org.insvtools.logger;

public interface Logger {
    void debug(String msg);
    void info(String msg);
    void error(String msg, Throwable err);
}
