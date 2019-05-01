package per.duyd.interview.me.util;

import org.slf4j.Logger;

public class LoggerHelper {
    public static String logError(Logger logger, String message, Exception ex) {
        logger.error(message);
        if (logger.isDebugEnabled()) {
            ex.printStackTrace();
        }
        return message;
    }

    public static String logError(Logger logger, String message) {
        logger.error(message);
        return message;
    }
}
