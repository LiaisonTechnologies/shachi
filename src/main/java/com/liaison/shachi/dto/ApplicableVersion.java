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

package com.liaison.shachi.dto;

import com.liaison.javabasics.commons.Util;
import com.liaison.shachi.api.request.frozen.LongValueSpecFrozen;
import com.liaison.shachi.model.VersioningModel;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.09 18:26
 */
public class ApplicableVersion {

    final LongValueSpecFrozen version;
    final VersioningModel scheme;

    public LongValueSpecFrozen getVersion() {
        return this.version;
    }
    public VersioningModel getScheme() {
        return this.scheme;
    }

    public ApplicableVersion(final VersioningModel scheme, final LongValueSpecFrozen version) {
        Util.ensureNotNull(scheme, this, "scheme", VersioningModel.class);
        Util.ensureNotNull(version, this, "version", LongValueSpecFrozen.class);
        this.scheme = scheme;
        this.version = version;
    }
}
