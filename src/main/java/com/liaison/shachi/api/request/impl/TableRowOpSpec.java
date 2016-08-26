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


public abstract class TableRowOpSpec<O extends TableRowOpSpec<O>> extends OperationSpec<O> {
    
    private static final long serialVersionUID = -7192144630587757596L;

    // ||========================================================================================||
    // ||    INSTANCE PROPERTIES                                                                 ||
    // ||----------------------------------------------------------------------------------------||
    
    private RowSpec<O> tableRow;
    
    // ||----(instance properties)---------------------------------------------------------------||
    
    // ||========================================================================================||
    // ||    INSTANCE METHODS                                                                    ||
    // ||----------------------------------------------------------------------------------------||
    
    public RowSpec<O> getTableRow() {
        return this.tableRow;
    }
    protected void setTableRow(final RowSpec<O> tableRow) throws IllegalArgumentException, IllegalStateException {
        prepMutation();
        Util.validateExactlyOnceParam(tableRow, this, "tableRow", RowSpec.class, this.tableRow);
        this.tableRow = tableRow;
    }
    
    // ||----(instance methods)------------------------------------------------------------------||

    // ||========================================================================================||
    // ||    CONSTRUCTORS                                                                        ||
    // ||----------------------------------------------------------------------------------------||

    public TableRowOpSpec(final Object handle, final HBaseContext context, final OperationControllerDefault parent) {
        super(handle, context, parent);
        this.tableRow = null;
    }
    
    // ||----(constructors)----------------------------------------------------------------------||
}
