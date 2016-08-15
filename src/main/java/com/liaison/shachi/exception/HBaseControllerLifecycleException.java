/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
