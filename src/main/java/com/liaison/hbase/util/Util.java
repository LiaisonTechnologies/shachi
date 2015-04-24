package com.liaison.hbase.util;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.BiPredicate;

public final class Util extends Uninstantiable {

    /**
     * Thread-safe, per the API:
     * https://docs.oracle.com/javase/8/docs/api/java/util/Base64.Encoder.html
     */
    public static final Base64.Encoder BASE64_ENC;
    /**
     * Thread-safe, per the API:
     * https://docs.oracle.com/javase/8/docs/api/java/util/Base64.Decoder.html
     */
    public static final Base64.Decoder BASE64_DEC;
    public static final byte[] HBASE_EMPTY = new byte[0];
    
    public static <T> boolean refEquals(final T ref1, final T ref2, final BiPredicate<? super T, ? super T> equals) {
        return (((ref1 == null) && (ref2 == null))
                ||
                ((ref1 != null) && (equals.test(ref1, ref2))));
    }
    public static boolean refEquals(final Object ref1, final Object ref2) {
        return refEquals(ref1,
                         ref2,
                         ((Object inner1, Object inner2) ->
                             {return inner1.equals(inner2);}));
    }
    public static boolean refEquals(final byte[] ref1, final byte[] ref2) {        
        return refEquals(ref1,
                         ref2,
                         ((byte[] inner1, byte[] inner2) ->
                             {return Arrays.equals(inner1, inner2);}));
    }
    
    public static String simplify(String str) {
        if (str != null) {
            str = str.trim();
            if (str.length() <= 0) {
                str = null;
            }
        }
        return str;
    }
    
    /**
     * Convert the string to bytes using the supplied Charset. If the string is null, returns null.
     * @param str String value
     * @param charset the charset to use to conver the String to bytes
     * @return byte[] representation of the String value, converted using the Charset specified
     * @throws IllegalArgumentException if the provided Charset is null
     */
    public static byte[] toBytes(final String str, Charset charset) throws IllegalArgumentException {
        if (str == null) {
            return null;
        }
        if (charset == null) {
            throw new IllegalArgumentException("Charset reference expected for conversion");
        }
        return str.getBytes(charset);
    }
    /**
     * Convert the string to bytes using the default Charset specified by
     * {@link Constants#CHARSET_DEFAULT} ({@value Constants#CHARSET_DEFAULT_STR}). If the string is
     * null, returns null.
     * @param str String value
     * @return byte[] representation of the String value, converted using the Charset specified by
     * {@link Constants#CHARSET_DEFAULT} ({@value Constants#CHARSET_DEFAULT_STR})
     */
    public static byte[] toBytes(final String str) {
        return toBytes(str, Constants.CHARSET_DEFAULT);
    }
    public static byte[] forHBase(final byte[] byteArr) {
        if (byteArr == null) {
            return HBASE_EMPTY;
        }
        return byteArr;
    }

    /**
     * Convert the bytes to String using the supplied Charset. If the bytes are null, returns null.
     * @param bytes byte[] representation of the String value
     * @param charset the charset to use to conver the bytes to String
     * @return the String value represented by the byte[], converted using the Charset specified
     * @throws IllegalArgumentException if the provided Charset is null
     */
    public static String toString(final byte[] bytes, Charset charset) throws IllegalArgumentException {
        if (bytes == null) {
            return null;
        }
        if (charset == null) {
            throw new IllegalArgumentException("Charset reference expected for conversion");
        }
        return new String(bytes, charset);
    }
    /**
     * Convert the bytes to String using the default Charset specified by
     * {@link Constants#CHARSET_DEFAULT} ({@value Constants#CHARSET_DEFAULT_STR}). 
     * If the bytes are null, returns null.
     * @param bytes byte[] representation of the String value
     * @return the String value represented by the byte[], converted using the Charset specified by
     * {@link Constants#CHARSET_DEFAULT} ({@value Constants#CHARSET_DEFAULT_STR})
     */
    public static String toString(final byte[] bytes) {
        return toString(bytes, Constants.CHARSET_DEFAULT);
    }
    
    public static String encode(final byte[] bytes) {
        return new String(BASE64_ENC.encode(bytes), Constants.CHARSET_DEFAULT);
    }
    
    public static byte[] decode(final String text) {
        return BASE64_DEC.decode(text);
    }
    
    public static byte[] copyOf(final byte[] inBytes) {
        if (inBytes == null) {
            return null;
        } else {
            return Arrays.copyOf(inBytes, inBytes.length);
        }
    }
    
    public static void ensureNotNull(final Object ref, final Class<?> enclosingType, final String varName, final Class<?> varType) throws IllegalArgumentException {
        if (ref == null) {
            throw new IllegalArgumentException(enclosingType.getSimpleName()
                                               + " requires non-null "
                                               + ((varType != null)?(varType.getSimpleName() + " "):"")
                                               + varName);
        }
    }
    public static void ensureNotNull(final Object ref, final Class<?> enclosingType, final String varName) throws IllegalArgumentException {
        ensureNotNull(ref, enclosingType, varName, null);
    }
    public static void ensureNotNull(final Object ref, final Object enclosingInstance, final String varName, final Class<?> varType) throws IllegalArgumentException {
        ensureNotNull(ref, enclosingInstance.getClass(), varName, varType);
    }
    public static void ensureNotNull(final Object ref, final Object enclosingInstance, final String varName) throws IllegalArgumentException {
        ensureNotNull(ref, enclosingInstance.getClass(), varName);
    }
    
    static {
        BASE64_ENC = Base64.getEncoder();
        BASE64_DEC = Base64.getDecoder();
    }
    
    private Util() {}
}
