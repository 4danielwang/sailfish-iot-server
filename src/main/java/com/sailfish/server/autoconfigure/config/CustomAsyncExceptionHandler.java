package com.sailfish.server.autoconfigure.config;

/**
 * @author wangpeixin
 * @since 2025/7/15 15:56
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (ex instanceof InterruptedException) {
            log.error("Async task interrupted. Method: {}, Parameters: {}",
                    method.getName(), Arrays.toString(params), ex);
        } else {
            log.error("Async task failed. Method: {}, Parameters: {}",
                    method.getName(), Arrays.toString(params), ex);
        }
    }

}
