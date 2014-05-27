/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import groovy.transform.CompileStatic

/**
 * Tax amount type for tax calculation.
 */
@CompileStatic
@XStreamAlias("TAX_AMOUNT")
class TaxAmount {
    @XStreamAsAttribute
    @XStreamAlias('DOCUMENT_AMOUNT')
    Double documentAmount


    @Override
    public String toString() {
        return "TaxAmount{" +
                "documentAmount=" + documentAmount +
                '}';
    }
}
