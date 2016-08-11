/**
 * Copyright 2015 Liaison Technologies, Inc.
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */
package com.liaison.shachi.api.request.fluid;

/**
 * An API specification-generating step which permits traceback to the parent/owning operation.
 * @author Branden Smith; Liaison Technologies, Inc.
 * @param <P> Type of the parent/owning operation
 */
public interface CriteriaSpecFluid<P> {
    /**
     * Return control to the parent/owning operation.
     * @return The operation specification which owns this criteria spec.
     */
    P and();
}
