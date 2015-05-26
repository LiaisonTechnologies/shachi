/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
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
