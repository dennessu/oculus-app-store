/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * indicator of tax inclusive flag.
 */
@CompileStatic
@XStreamAlias('INCLUSIVE_TAX_INDICATORS')
class InclusiveTaxIndicator {
    @XStreamAlias('FULLY_INCLUSIVE')
    Boolean fullyInclusive


    @Override
    public String toString() {
        return "InclusiveTaxIndicator{" +
                "fullyInclusive=" + fullyInclusive +
                '}';
    }
}
