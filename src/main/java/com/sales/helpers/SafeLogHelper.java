package com.sales.helpers;

import com.sales.utils.Utils;
import org.slf4j.Logger;

import java.util.Arrays;

public final class SafeLogHelper {

    private SafeLogHelper() {}
    private  static SafeLogHelper safeLogObj;


    public static SafeLogHelper getInstance(){
        if(safeLogObj == null){
            return new SafeLogHelper();
        }
        return safeLogObj;
    }

    public void info(Logger logger, String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            Object[] processed = Arrays.stream(objects)
                    .map((b) -> Utils.sanitizeForLog(b.toString()))
                    .toArray();
            logger.debug(message, processed);
        }
    }
    public void warn(Logger logger, String message,Object... objects) {
        if (logger.isWarnEnabled()) {
            Object[] processed = Arrays.stream(objects)
                    .map((b) -> Utils.sanitizeForLog(b.toString()))
                    .toArray();
            logger.warn(message,Utils.sanitizeForLog(Arrays.toString(processed)));
        }
    }

    public void error(Logger logger, String message,Object... objects) {
        Object[] processed = Arrays.stream(objects)
                .map((b) -> Utils.sanitizeForLog(b.toString()))
                .toArray();
        if (logger.isErrorEnabled()) {
            logger.warn(message,processed);
        }
    }
}
