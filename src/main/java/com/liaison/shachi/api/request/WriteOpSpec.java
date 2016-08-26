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
package com.liaison.shachi.api.request;

import com.liaison.shachi.api.request.fluid.WriteOpSpecFluid;
import com.liaison.shachi.api.request.frozen.WriteOpSpecFrozen;
import com.liaison.shachi.api.request.impl.SpecState;

/**
 * Specifies a <strong>WRITE</strong> operation, whose API consists of the union of ways in which
 * it may be <em>specified</em> while in a <em>fluid<state> and the ways in which it may be
 * <em>executed</em> (or referenced) while in a <em>frozen</em> state.
 * @see {@link SpecState}
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <Z> represents the implementation type, so that the typed instance may be returned in a
 * fluent/chaining API
 */
public interface WriteOpSpec<Z> extends WriteOpSpecFluid<Z>, WriteOpSpecFrozen {

}
