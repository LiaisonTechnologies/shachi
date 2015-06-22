/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.util;

import com.liaison.commons.Uninstantiable;

import java.nio.charset.Charset;

public final class Constants extends Uninstantiable {
    
    public static final String CHARSET_DEFAULT_STR = "UTF-8";
    public static final Charset CHARSET_DEFAULT = Charset.forName(CHARSET_DEFAULT_STR);
    
    private Constants() {}
}
