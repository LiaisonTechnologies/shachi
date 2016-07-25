package com.liaison.hbase.dto;

/**
 * Created by BSMITH on 2016.07.25.
 */
public class ParsedVersionQualifier {

    public static class Builder {
        private byte[] unversionedQualBytes;
        private Long version;


    }

    private ParsedVersionQualifier(final Builder build) {

    }
}
