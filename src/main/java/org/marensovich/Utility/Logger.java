package org.marensovich.Utils;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private static final Logger logger = (Logger) LogManager.getLogger(Logger.class);

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void info(String format, Object... args) {
        logger.info(format, args);
    }

    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }

    public void error(String format, Object... args) {
        logger.error(format, args);
    }

    public void debug(String format, Object... args) {
        logger.debug(format, args);
    }
}
