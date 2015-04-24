package com.liaison.hbase.model;

/**
 * TODO... this enum is likely to be replaced with a less restrictive strategy class which would
 * define logic for generating a timestamp for a field
 * @author Branden Smith; Liaison Technologies, Inc.
 *
 */
public enum TimestampStrategy {
    SYSTEM, CLIENT, TABLE;
}
