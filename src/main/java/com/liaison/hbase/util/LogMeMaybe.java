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
    private final String generate(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        final StringBuilder logStr;
        String logPart;
        
        logStr = new StringBuilder();
        if (toLogArray != null) {
            if (methodName != null) {
                logStr.append(LOG_METHODNAMEPREFIX_BEFORE);
                logStr.append(methodName);
                logStr.append(LOG_METHODNAMEPREFIX_AFTER);
            }
            for (Supplier<String> toLog : toLogArray) {
                if (toLog != null) {
                    logPart = toLog.get();
                    if (logPart != null) {
                        logStr.append(logPart);
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
    private final String generate(final String methodName, final Supplier<String>... toLogArray) {
        return generate(null, methodName, toLogArray);
    }
    
    @SafeVarargs
    private final void doLog(final String methodName, final Supplier<Boolean> enabledCheck, final BiConsumer<String, Throwable> loggerWithExc, final Consumer<String> loggerWithoutExc, final Throwable exc, final Supplier<String>... toLogArray) {
        if (enabledCheck.get().booleanValue()) {
            if (exc != null) {
                loggerWithExc.accept(generate(exc, methodName, toLogArray), exc);
            } else {
                loggerWithoutExc.accept(generate(methodName, toLogArray));
            }
        }
    }
    
    @SafeVarargs
    public final String enter(final Supplier<String>... toLogArray) {
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
    public final void trace(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        doLog(methodName, log::isTraceEnabled, log::trace, log::trace, exc, toLogArray);
    }
    @SafeVarargs
    public final void trace(final String methodName, final Supplier<String>... toLogArray) {
        trace(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void trace(final Throwable exc, final Supplier<String>... toLogArray) {
        trace(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void trace(final Supplier<String>... toLogArray) {
        trace(((String) null), toLogArray);
    }
    
    @SafeVarargs
    public final void debug(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        doLog(methodName, log::isDebugEnabled, log::debug, log::debug, exc, toLogArray);
    }
    @SafeVarargs
    public final void debug(final String methodName, final Supplier<String>... toLogArray) {
        debug(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void debug(final Throwable exc, final Supplier<String>... toLogArray) {
        debug(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void debug(final Supplier<String>... toLogArray) {
        debug(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void info(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        doLog(methodName, log::isInfoEnabled, log::info, log::info, exc, toLogArray);
    }
    @SafeVarargs
    public final void info(final String methodName, final Supplier<String>... toLogArray) {
        info(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void info(final Throwable exc, final Supplier<String>... toLogArray) {
        info(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void info(final Supplier<String>... toLogArray) {
        info(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void warn(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        doLog(methodName, log::isWarnEnabled, log::warn, log::warn, exc, toLogArray);
    }
    @SafeVarargs
    public final void warn(final String methodName, final Supplier<String>... toLogArray) {
        warn(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void warn(final Throwable exc, final Supplier<String>... toLogArray) {
        warn(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void warn(final Supplier<String>... toLogArray) {
        warn(((String) null), toLogArray);
    }

    @SafeVarargs
    public final void error(final Throwable exc, final String methodName, final Supplier<String>... toLogArray) {
        doLog(methodName, log::isErrorEnabled, log::error, log::error, exc, toLogArray);
    }
    @SafeVarargs
    public final void error(final String methodName, final Supplier<String>... toLogArray) {
        error(null, methodName, toLogArray);
    }
    @SafeVarargs
    public final void error(final Throwable exc, final Supplier<String>... toLogArray) {
        error(exc, null, toLogArray);
    }
    @SafeVarargs
    public final void error(final Supplier<String>... toLogArray) {
        error(((String) null), toLogArray);
    }
    
    public LogMeMaybe(final Class<?> sourceClass) throws IllegalArgumentException {
        Util.ensureNotNull(sourceClass, this, "sourceClass", Class.class);
        this.log = LoggerFactory.getLogger(sourceClass);
    }
}
