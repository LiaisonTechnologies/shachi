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
import com.liaison.shachi.api.request.fluid.ColSpecReadFluid;
import com.liaison.shachi.model.FamilyHB;
import com.liaison.shachi.model.QualHB;
import com.liaison.shachi.util.TreeNodeRoot;


/**
 * TODO
 * @author Branden Smith; Liaison Technologies, Inc.
 */
public class ColSpecReadConfined extends TreeNodeRoot<ColSpecReadConfined> implements ColSpecReadFluid<ColSpecReadConfined> {

    private final ColSpecRead<ReadOpSpecDefault> colSpecRead;

    @Override
    public ColSpecReadConfined handle(Object handle) throws IllegalStateException {
        colSpecRead.handle(handle);
        return self();
    }
    @Override
    public ColSpecReadConfined fam(final FamilyHB family) throws IllegalStateException, IllegalArgumentException {
        colSpecRead.fam(family);
        return self();
    }
    @Override
    public ColSpecReadConfined qual(final QualHB qual) throws IllegalStateException, IllegalArgumentException {
        colSpecRead.qual(qual);
        return self();
    }
    @Override
    public ColSpecReadConfined optional() throws IllegalStateException, IllegalArgumentException {
        colSpecRead.optional();
        return self();
    }
    @Override
    public LongValueSpecConfinedParent<ColSpecReadConfined, ColSpecRead<ReadOpSpecDefault>> version() throws IllegalStateException, IllegalArgumentException {
        return
            new LongValueSpecConfinedParent<ColSpecReadConfined,
                                            ColSpecRead<ReadOpSpecDefault>>
                    (self(), colSpecRead.version());
    }
    @Override
    public ColSpecReadConfined version(final long version) throws IllegalStateException, IllegalArgumentException {
        return self();
    }

    @Override
    protected ColSpecReadConfined self() {
        return this;
    }
    
    public ColSpecReadConfined(final ColSpecRead<ReadOpSpecDefault> colSpecRead) {
        Util.ensureNotNull(colSpecRead, this, "colSpecRead", ColSpecRead.class);
        this.colSpecRead = colSpecRead;
    }
}
