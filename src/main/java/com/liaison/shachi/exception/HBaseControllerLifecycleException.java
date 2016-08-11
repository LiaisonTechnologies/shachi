/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.exception;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HBaseControllerLifecycleException extends HBaseException {

    private static final long serialVersionUID = 1262708278144614075L;
    
    private static final String COMBINED_MESSAGE_FIRST = "{first:";
    private static final String COMBINED_MESSAGE_SECOND = "|second:";
    private static final String COMBINED_MESSAGE_SUFFIX = "}";

    private static final Map<Closeable, IOException> createInternalResourceExceptionsMap() {
        return new ConcurrentHashMap<Closeable, IOException>();
    }
    
    public static HBaseControllerLifecycleException mergeToNew(final HBaseControllerLifecycleException vlcExc1, final HBaseControllerLifecycleException vlcExc2) {
        final HBaseControllerLifecycleException vlcExcNew;
        final StringBuilder message;
        
        message = new StringBuilder();
        message.append(COMBINED_MESSAGE_FIRST);
        message.append(vlcExc1.getMessage());
        message.append(COMBINED_MESSAGE_SECOND);
        message.append(vlcExc2.getMessage());
        message.append(COMBINED_MESSAGE_SUFFIX);
        
        vlcExcNew = new HBaseControllerLifecycleException(message.toString(), vlcExc1.getCause());
        vlcExcNew.addSuppressed(vlcExc2.getCause());
        vlcExcNew.internalResourceExceptionsMap.putAll(vlcExc1.internalResourceExceptionsMap);
        vlcExcNew.internalResourceExceptionsMap.putAll(vlcExc2.internalResourceExceptionsMap);
        
        return vlcExcNew;
    }
    
    private final Map<Closeable, IOException> internalResourceExceptionsMap;
    
    public void addInternalResourceException(final Closeable res, final IOException ioExc) {
        if (ioExc != null) {
            this.internalResourceExceptionsMap.put(res, ioExc);
        }
    }
    public Map<Closeable, IOException> getInternalResourceExceptions() {
        return Collections.unmodifiableMap(this.internalResourceExceptionsMap);
    }
    
    @Override
    public String getLocalizedMessage() {
        final StringBuilder strGen;
        strGen = new StringBuilder();
        strGen.append(super.getLocalizedMessage());
        strGen.append(" (resource-exceptions:");
        strGen.append(this.internalResourceExceptionsMap);
        strGen.append(")");
        return strGen.toString();
    }
    
    public HBaseControllerLifecycleException(String message) {
        super(message);
        this.internalResourceExceptionsMap = createInternalResourceExceptionsMap();
    }
    public HBaseControllerLifecycleException(String message, Throwable cause) {
        super(message, cause);
        this.internalResourceExceptionsMap = createInternalResourceExceptionsMap();
    }
}
