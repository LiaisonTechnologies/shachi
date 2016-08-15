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
import com.liaison.shachi.context.HBaseContext;
import com.liaison.shachi.exception.SpecValidationException;

import java.io.Serializable;

public abstract class OperationSpec<O extends OperationSpec<O>> extends StatefulSpec<O, OperationControllerDefault> implements Serializable {
    
    private static final long serialVersionUID = 5533663131351737507L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private final Object handle;
    private final HBaseContext context;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS                                                                    ||
    // ||----------------------------------------------------------------------------------------||
    
    /**
     * TODO
     * @return
     */
    public final Object getHandle() {
        return this.handle;
    }
    
    /**
     * TODO
     * @return
     */
    protected HBaseContext getContext() {
        return this.context;
    }
    
    /**
     * TODO
     * @return
     * @throws SpecValidationException
     */
    public final OperationControllerDefault then() throws SpecValidationException {
        freezeRecursive();
        return getParent();
    }
    public final OperationControllerDefault done() throws SpecValidationException {
        return then();
    }
    
    @Override
    public final int prepareHashCode() {
        return this.handle.hashCode();
    }
    
    /**
     * TODO
     * @param otherOpSpec
     * @return
     */
    protected abstract boolean deepEquals(final OperationSpec<?> otherOpSpec);
    
    @Override
    public final boolean equals(final Object otherObj) {
        final OperationSpec<?> otherOpSpec;
        if (otherObj instanceof OperationSpec) {
            otherOpSpec = (OperationSpec<?>) otherObj;
            return ((Util.refEquals(this.handle, otherOpSpec.handle))
                    &&
                    deepEquals(otherOpSpec));
        }
        return false;
    }
    
    // ||----(instance methods)------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public OperationSpec(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(parent);
        Util.ensureNotNull(handle, this, "handle", Object.class);
        this.handle = handle;
        Util.ensureNotNull(context, this, "context", HBaseContext.class);
        this.context = context;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
