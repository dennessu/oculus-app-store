/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Request Status type for tax calculation output.
 */
@CompileStatic
@XStreamAlias('REQUEST_STATUS')
class RequestStatus {
    @XStreamAlias('IS_SUCCESS')
    Boolean isSuccess

    @XStreamAlias('IS_PARTIAL_SUCCESS')
    Boolean isPartialSuccess

    @XStreamAlias('ERROR')
    List<SabrixError> error


    @Override
    public String toString() {
        return "RequestStatus{" +
                "isSuccess=" + isSuccess +
                ", isPartialSuccess=" + isPartialSuccess +
                ", error=" + error +
                '}';
    }
}
