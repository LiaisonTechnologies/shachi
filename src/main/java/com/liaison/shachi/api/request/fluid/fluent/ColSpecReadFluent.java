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
package com.liaison.shachi.api.request.fluid.fluent;

import com.liaison.shachi.api.request.fluid.ColSpecReadFluid;
import com.liaison.shachi.api.request.fluid.CriteriaSpecFluid;

/**
 * Denotes an API implementation of {@link ColSpecReadFluid} which can be retraced to its parent in
 * the API spec tree by calling {@link CriteriaSpecFluid#and()}.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <C> The type of the current element in the API/spec tree
 * @param <P> The type of the parent/owning element in the API/spec tree
 */
public interface ColSpecReadFluent<C extends ColSpecReadFluent<C, P>, P> extends ColSpecReadFluid<C>, CriteriaSpecFluid<P> { }
