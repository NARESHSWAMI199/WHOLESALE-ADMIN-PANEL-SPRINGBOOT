package com.sales.helpers;

import com.sales.utils.Utils;
import org.slf4j.Logger;

import java.util.Arrays;

public final class SafeLogHelper implements com.sales.helpers.Logger {

    private SafeLogHelper() {}
    private  static SafeLogHelper safeLogObj;


    public static SafeLogHelper getInstance(){
        if(safeLogObj == null){
            return new SafeLogHelper();
        }
        return safeLogObj;
    }

    @Override
    public void info(Logger logger,String message, Object... object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, Utils.sanitizeForLog(Arrays.toString(object)));
        }
    }

    @Override
    public void warn(Logger log, String message,Object... object) {
        if (log.isWarnEnabled()) {
            log.warn(message,Utils.sanitizeForLog(Arrays.toString(object)));
        }
    }

    @Override
    public void error(Logger log, String message,Object... object) {
        if (log.isErrorEnabled()) {
            log.warn(message,Utils.sanitizeForLog(Arrays.toString(object)));
        }
    }
}
