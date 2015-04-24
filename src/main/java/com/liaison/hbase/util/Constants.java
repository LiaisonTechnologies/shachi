package com.liaison.hbase.util;

import java.nio.charset.Charset;

public final class Constants extends Uninstantiable {
    
    public static final String CHARSET_DEFAULT_STR = "UTF-8";
    public static final Charset CHARSET_DEFAULT = Charset.forName(CHARSET_DEFAULT_STR);
    
    private Constants() {}
}
