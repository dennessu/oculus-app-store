/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Exempt rule for tax calculation.
 */
@CompileStatic
@XStreamAlias('IS_EXEMPT')
class IsExempt {
    @XStreamAlias('ALL')
    Boolean all


    @Override
    public String toString() {
        return "Exempt{" +
                "all=" + all +
                '}';
    }
}
