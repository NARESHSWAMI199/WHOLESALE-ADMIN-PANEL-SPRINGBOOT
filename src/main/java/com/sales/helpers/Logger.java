package com.sales.helpers;

public interface Logger {
     void info(org.slf4j.Logger logger, String message, Object... object);
     void warn(org.slf4j.Logger logger, String message, Object... object);
     void error(org.slf4j.Logger logger, String message, Object... object);
}
