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

package com.liaison.shachi.model.ser;

import com.liaison.shachi.exception.CellDeserializationException;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.07.15 15:09
 */
@FunctionalInterface
public interface CellDeserializer {
    /**
     *
     * @param hbaseCellValue
     * @return
     * @throws CellDeserializationException if the bytes retrieved from HBase cannot be decoded
     */
    Object deserialize(byte[] hbaseCellValue) throws CellDeserializationException;
}
