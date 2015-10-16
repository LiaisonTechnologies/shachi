/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.hbase.context;

import com.liaison.hbase.model.Name;
import com.liaison.hbase.model.TableModel;
import com.liaison.serialization.BytesUtil;
import com.liaison.serialization.DefensiveCopyStrategy;

import java.nio.charset.Charset;

public class DirectoryPrefixedTableNamingStrategy implements TableNamingStrategy {

    private final byte[] homeDir;
    
    @Override
    public Name generate(final TableModel model) {
        final int nameLength;
        final byte[] finalName;
        final byte[] modelName;
        
        if (this.homeDir == null) {
            return model.getName();
        } else {
            modelName = model.getName().getValue(DefensiveCopyStrategy.NEVER);
            nameLength = this.homeDir.length + modelName.length;
            finalName = new byte[nameLength];
            System.arraycopy(this.homeDir, 0, finalName, 0, this.homeDir.length);
            System.arraycopy(modelName, 0, finalName, this.homeDir.length, modelName.length);
            return Name.of(finalName, DefensiveCopyStrategy.NEVER);
        }
    }

    public DirectoryPrefixedTableNamingStrategy(final String homeDirStr, Charset homeDirEncoding) {
        if ((homeDirStr == null) || (homeDirStr.length() <= 0)) {
            this.homeDir = null;
        } else {
            if (homeDirEncoding == null) {
                this.homeDir = BytesUtil.toBytes(homeDirStr);
            } else {
                this.homeDir = BytesUtil.toBytes(homeDirStr, homeDirEncoding);
            }
        }
    }
    public DirectoryPrefixedTableNamingStrategy(final String homeDirStr) {
        this(homeDirStr, null);
    }
}
