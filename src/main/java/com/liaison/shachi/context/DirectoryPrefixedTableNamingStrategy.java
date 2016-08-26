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
package com.liaison.shachi.context;

import com.liaison.javabasics.serialization.BytesUtil;
import com.liaison.javabasics.serialization.DefensiveCopyStrategy;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.TableModel;

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
