/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogMeMaybe {
    
    private static final String LOG_METHODNAMEPREFIX_BEFORE = "[";
    private static final String LOG_METHODNAMEPREFIX_AFTER = "] ";
    private static final String LOG_ENTER = ">>> ";
    private static final String LOG_LEAVE = "<<< ";
    
    private final Logger log;

    @SafeVarargs
    private final String generate(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        final StringBuilder logStr;
        Object logPart;
        
        logStr = new StringBuilder();
        if (toLogArray != null) {
            if (methodName != null) {
                logStr.append(LOG_METHODNAMEPREFIX_BEFORE);
                logStr.append(methodName);
                logStr.append(LOG_METHODNAMEPREFIX_AFTER);
            }
            for (Supplier<Object> toLog : toLogArray) {
                if (toLog != null) {
                    logPart = toLog.get();
                    if (logPart != null) {
                        logStr.append(logPart.toString());
                    }
                }
            }
            if (exc != null) {
                logStr.append(" | ");
                logStr.append(exc.toString());
            }
        }
        return logStr.toString();
    }
    
    @SafeVarargs
    private final String generate(final String methodName, final Supplier<Object>... toLogArray) {
        return generate(null, methodName, toLogArray);
    }
    
    @SafeVarargs
    private final void doLog(final String methodName, final Supplier<Boolean> enabledCheck, final BiConsumer<String, Throwable> loggerWithExc, final Consumer<String> loggerWithoutExc, final Throwable exc, final Supplier<Object>... toLogArray) {
        if (enabledCheck.get().booleanValue()) {
            if (exc != null) {
                loggerWithExc.accept(generate(exc, methodName, toLogArray), exc);
            } else {
                loggerWithoutExc.accept(generate(methodName, toLogArray));
            }
        }
    }
    
    @SafeVarargs
    public final String enter(final Supplier<Object>... toLogArray) {
        final String methodName;
        if (log.isTraceEnabled()) {
            methodName = generate(null, toLogArray);
            trace(()->LOG_ENTER, ()->methodName);
            return methodName;
        } else {
            return null;
        }
    }
    
    public final void leave(final String methodName) {
        trace(()->LOG_LEAVE, ()->methodName);
    }
    
    @SafeVarargs
    public final void trace(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        doLog(methodName, log::isTraceEnabled, log::trace, log::trace, exc, toLogArray);
    }
    @SafeVarargs
    public final void trace(final String methodName, final Supplier<Object>... toLogArray) {
        trace(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void trace(final Throwable exc, final Supplier<Object>... toLogArray) {
        trace(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void trace(final Supplier<Object>... toLogArray) {
        trace(((String) null), toLogArray);
    }
    
    @SafeVarargs
    public final void debug(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        doLog(methodName, log::isDebugEnabled, log::debug, log::debug, exc, toLogArray);
    }
    @SafeVarargs
    public final void debug(final String methodName, final Supplier<Object>... toLogArray) {
        debug(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void debug(final Throwable exc, final Supplier<Object>... toLogArray) {
        debug(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void debug(final Supplier<Object>... toLogArray) {
        debug(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void info(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        doLog(methodName, log::isInfoEnabled, log::info, log::info, exc, toLogArray);
    }
    @SafeVarargs
    public final void info(final String methodName, final Supplier<Object>... toLogArray) {
        info(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void info(final Throwable exc, final Supplier<Object>... toLogArray) {
        info(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void info(final Supplier<Object>... toLogArray) {
        info(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void warn(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        doLog(methodName, log::isWarnEnabled, log::warn, log::warn, exc, toLogArray);
    }
    @SafeVarargs
    public final void warn(final String methodName, final Supplier<Object>... toLogArray) {
        warn(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void warn(final Throwable exc, final Supplier<Object>... toLogArray) {
        warn(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void warn(final Supplier<Object>... toLogArray) {
        warn(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void error(final Throwable exc, final String methodName, final Supplier<Object>... toLogArray) {
        doLog(methodName, log::isErrorEnabled, log::error, log::error, exc, toLogArray);
    }
    @SafeVarargs
    public final void error(final String methodName, final Supplier<Object>... toLogArray) {
        error(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void error(final Throwable exc, final Supplier<Object>... toLogArray) {
        error(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void error(final Supplier<Object>... toLogArray) {
        error(((String) null), toLogArray);
    }

    public final void trace(final String logMsg) {
        log.trace(logMsg);
    }
    public final void trace(final String logMsg, final Throwable exc) {
        log.trace(logMsg, exc);
    }
    public final void trace(final String methodName, final String logMsg) {
        trace(methodName, ()->logMsg);
    }
    public final void trace(final String methodName, final String logMsg, final Throwable exc) {
        trace(exc, methodName, ()->logMsg);
    }

    public final void debug(final String logMsg) {
        log.debug(logMsg);
    }
    public final void debug(final String logMsg, final Throwable exc) {
        log.debug(logMsg, exc);
    }
    public final void debug(final String methodName, final String logMsg) {
        debug(methodName, ()->logMsg);
    }
    public final void debug(final String methodName, final String logMsg, final Throwable exc) {
        debug(exc, methodName, ()->logMsg);
    }

    public final void info(final String logMsg) {
        log.info(logMsg);
    }
    public final void info(final String logMsg, final Throwable exc) {
        log.info(logMsg, exc);
    }
    public final void info(final String methodName, final String logMsg) {
        info(methodName, ()->logMsg);
    }
    public final void info(final String methodName, final String logMsg, final Throwable exc) {
        info(exc, methodName, ()->logMsg);
    }
    
    public final void warn(final String logMsg) {
        log.warn(logMsg);
    }
    public final void warn(final String logMsg, final Throwable exc) {
        log.warn(logMsg, exc);
    }
    public final void warn(final String methodName, final String logMsg) {
        warn(methodName, ()->logMsg);
    }
    public final void warn(final String methodName, final String logMsg, final Throwable exc) {
        warn(exc, methodName, ()->logMsg);
    }
    
    public final void error(final String logMsg) {
        log.error(logMsg);
    }
    public final void error(final String logMsg, final Throwable exc) {
        log.error(logMsg, exc);
    }
    public final void error(final String methodName, final String logMsg) {
        error(methodName, ()->logMsg);
    }
    public final void error(final String methodName, final String logMsg, final Throwable exc) {
        error(exc, methodName, ()->logMsg);
    }
    
    public LogMeMaybe(final Class<?> sourceClass) throws IllegalArgumentException {
        Util.ensureNotNull(sourceClass, this, "sourceClass", Class.class);
        this.log = LoggerFactory.getLogger(sourceClass);
    }
}
