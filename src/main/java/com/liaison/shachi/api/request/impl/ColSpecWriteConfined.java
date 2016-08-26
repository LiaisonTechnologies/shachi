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
package com.liaison.shachi.api.request.impl;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.api.request.fluid.ColSpecWriteFluid;
import com.liaison.shachi.dto.Empty;
import com.liaison.shachi.dto.Value;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ColSpecWriteConfined implements ColSpecWriteFluid<ColSpecWriteConfined> {

    private final ColSpecWrite<WriteOpSpecDefault> colSpecWrite;

    @Override
    public ColSpecWriteConfined handle(Object handle) throws IllegalStateException {
        colSpecWrite.handle(handle);
        return this;
    }
    @Override
    public ColSpecWriteConfined fam(final FamilyHB family) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.fam(family);
        return this;
    }
    @Override
    public ColSpecWriteConfined qual(final QualHB qual) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.qual(qual);
        return this;
    }
    @Override
    public ColSpecWriteConfined version(final long version) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.version(version);
        return this;
    }
    @Override
    public ColSpecWriteConfined ts(final long ts) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.ts(ts);
        return this;
    }
    @Override
    public ColSpecWriteConfined value(final Value value) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.value(value);
        return this;
    }
    @Override
    public ColSpecWriteConfined empty(final Empty empty) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.empty(empty);
        return this;
    }
    @Override
    public ColSpecWriteConfined content(final Object dataObj) throws IllegalStateException, IllegalArgumentException {
        colSpecWrite.content(dataObj);
        return this;
    }

    public ColSpecWriteConfined(final ColSpecWrite<WriteOpSpecDefault> colSpecWrite) {
        Util.ensureNotNull(colSpecWrite, this, "colSpecWrite", ColSpecWrite.class);
        this.colSpecWrite = colSpecWrite;
    }

}
