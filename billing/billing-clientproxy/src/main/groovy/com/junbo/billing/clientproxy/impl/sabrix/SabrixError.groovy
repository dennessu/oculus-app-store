/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Error type for Sabrix tax engine.
 */
@CompileStatic
@XStreamAlias('ERROR')
class SabrixError {
    @XStreamAlias('CODE')
    String code

    @XStreamAlias('DESCRIPTION')
    String description

    @XStreamAlias('ERROR_LOCATION_PATH')
    String errorLocationPath


    @Override
    public String toString() {
        return "SabrixError{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", errorLocationPath='" + errorLocationPath + '\'' +
                '}';
    }
}
