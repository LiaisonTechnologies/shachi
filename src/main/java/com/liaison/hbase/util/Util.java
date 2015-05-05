package com.liaison.hbase.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumSet;
import java.util.function.BiPredicate;

import org.slf4j.Logger;

import com.liaison.hbase.context.DefensiveCopyStrategy;
import com.liaison.hbase.context.HBaseContext;

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
    
    public static final String INDENT = "    ";
    
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
    private static byte[] byteActionWithContext(final byte[] bytes, final HBaseContext context, final EnumSet<DefensiveCopyStrategy> copyIfTheseStrategies) throws IllegalArgumentException {
        Util.ensureNotNull(copyIfTheseStrategies,
                           "Util#byteActionWithContext",
                           "copyIfTheseStrategies",
                           EnumSet.class);
        Util.ensureNotNull(context,
                           "Util#byteActionWithContext",
                           "context",
                           HBaseContext.class);
        if (copyIfTheseStrategies.contains(context.getDefensiveCopyStrategy())) {
            return copyOf(bytes);
        } else {
            return bytes;
        }
    }
    /**
     * Prepares the given <b>input</b> byte array to be <b>SET</b> in a framework object. Depending
     * on the specification of the provided context, optionally makes a defensive copy rather than
     * setting the framework object to use the exact reference provided. Specifically, if the
     * defensive copy strategy specified by the context is one of the strategies specified in
     * {@link DefensiveCopyStrategy#COPY_ON_SET}, then make the copy; otherwise, set using the
     * original reference. 
     * @param inBytes original byte[] reference to which a framework object's internal byte array
     * reference is being set; a defensive copy may be made, depending on the context
     * @param context HBaseContext whose {@link DefensiveCopyStrategy} specifies whether a
     * defensive copy of the input byte array will be made prior to setting
     * @return the value to use when setting the framework object; either the original reference or
     * a defensive copy, depending on the logic specified above
     * @throws IllegalArgumentException if context is null
     */
    public static byte[] setWithContext(final byte[] inBytes, final HBaseContext context) throws IllegalArgumentException {
        return byteActionWithContext(inBytes, context, DefensiveCopyStrategy.COPY_ON_SET);
    }
    /**
     * Prepares the given <b>existing/internal</b> byte array to be <b>RETURNED</b> from a
     * framework object. Depending on the specification of the provided context, optionally makes a
     * defensive copy rather than returning the internal byte array maintained within the framework
     * object. Specifically, if the defensive copy strategy specified by the context is one of the
     * strategies specified in {@link DefensiveCopyStrategy#COPY_ON_GET}, then make the copy;
     * otherwise, return the original reference. 
     * @param inBytes a framework object's internal byte array; a defensive copy may be made,
     * depending on the context
     * @param context HBaseContext whose {@link DefensiveCopyStrategy} specifies whether a
     * defensive copy of the internal byte array will be made prior to the get return
     * @return the value which the get operation should return to the client; either the given
     * reference to the internal byte array or a defensive copy, depending on the logic specified
     * above
     * @throws IllegalArgumentException if context is null
     */
    public static byte[] getWithContext(final byte[] storedBytes, final HBaseContext context) throws IllegalArgumentException {
        return byteActionWithContext(storedBytes, context, DefensiveCopyStrategy.COPY_ON_GET);
    }
    
    public static void ensureNotNull(final Object ref, final String closureName, final String varName, final Class<?> varType) throws IllegalArgumentException {
        if (ref == null) {
            throw new IllegalArgumentException(closureName
                                               + " requires non-null "
                                               + ((varType != null)?(varType.getSimpleName() + " "):"")
                                               + varName);
        }
    }
    public static void ensureNotNull(final Object ref, final Class<?> enclosingType, final String varName, final Class<?> varType) throws IllegalArgumentException {
        ensureNotNull(ref, enclosingType.getSimpleName(), varName, varType);
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
    
    public static <E extends Enum<E>> void verifyState(final E expectedState, final E currentState, final Object stateLock) throws IllegalStateException {
        synchronized(stateLock) {
            verifyState(expectedState, currentState);
        }
    }
    public static <E extends Enum<E>> void verifyState(final E expectedState, final E currentState) throws IllegalStateException {
        if (expectedState != currentState) {
            throw new IllegalStateException("Expected state: " + expectedState
                                            + "; current state: " + currentState);
        }
    }
    
    public static void traceLog(final Logger log, final String logMethodName, String logMsg, final Throwable exc) {
        // >>>>> LOG >>>>>
        if (log.isTraceEnabled()) {
            logMsg = "[" + logMethodName + "] " + logMsg;
            if (exc != null) {
                log.trace(logMsg, exc);
            } else {
                log.trace(logMsg);
            }
        } else if (log.isDebugEnabled()) {
            if (exc != null) {
                log.debug(logMsg, exc);
            } else {
                log.debug(logMsg);
            }
        }
        // <<<<< log <<<<<
    }
    public static void traceLog(final Logger log, final String logMethodName, String logMsg) {
        traceLog(log, logMethodName, logMsg, null);
    }
    
    public static <X> X validateExactlyOnceParam(final X param, final Object enclosingInstance, final String paramName, final Class<X> paramClass, final Object valueSetTarget) throws IllegalArgumentException, IllegalStateException {
        ensureNotNull(param, enclosingInstance, paramName, paramClass);
        validateExactlyOnce(paramName, paramClass, valueSetTarget);
        return param;
    }
    public static void validateExactlyOnce(final String entityName, final Class<?> entityClass, final Object valueSetTarget) throws IllegalStateException {
        if (valueSetTarget != null) {
            throw new IllegalStateException(entityClass.getSimpleName()
                                            + " reference for "
                                            + entityName
                                            + " may only be set once, and is already initialized: "
                                            + valueSetTarget);
        }
    }
    
    public static void indent(final Appendable strGen, final int indentCount) throws IllegalStateException {
        for (int counter = 0; counter < indentCount; counter++) {
            try {
                strGen.append(INDENT);
            } catch (IOException ioExc) {
                throw new IllegalStateException("Failed adding indentation ("
                                                + (counter + 1)
                                                + " of "
                                                + indentCount
                                                + ")");
            }
        }
    }
    public static void appendIndented(final Appendable strGen, final int indentCount, final Object... objListForLine) throws IllegalStateException {
        if (objListForLine != null) {
            indent(strGen, indentCount);
            for (Object obj : objListForLine) {
                if (obj != null) {
                    try {
                        strGen.append(obj.toString());
                    } catch (IOException ioExc) {
                        throw new IllegalStateException("Failed to append (" + obj + ")");
                    }
                }
            }
        }
    }
    
    static {
        BASE64_ENC = Base64.getEncoder();
        BASE64_DEC = Base64.getDecoder();
    }
    
    private Util() {}
}
