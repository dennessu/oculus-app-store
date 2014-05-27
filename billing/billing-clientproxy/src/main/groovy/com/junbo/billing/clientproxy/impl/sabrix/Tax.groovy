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
 * Tax level output for tax calculation.
 */
@CompileStatic
@XStreamAlias("TAX")
class Tax {
    @XStreamAsAttribute
    @XStreamAlias('AUTHORITY_TYPE')
    String authorityType

    @XStreamAsAttribute
    @XStreamAlias('AUTHORITY_NAME')
    String authorityName

    @XStreamAsAttribute
    @XStreamAlias('IS_EXEMPT')
    Boolean isExempt

    @XStreamAsAttribute
    @XStreamAlias('TAX_TYPE')
    String taxType

    @XStreamAsAttribute
    @XStreamAlias('TAX_RATE')
    Double taxRate

    @XStreamAsAttribute
    @XStreamAlias('TAX_AMOUNT')
    TaxAmount taxAmount


    @Override
    public String toString() {
        return "Tax{" +
                "authorityType='" + authorityType + '\'' +
                ", authorityName='" + authorityName + '\'' +
                ", isExempt=" + isExempt +
                ", taxType='" + taxType + '\'' +
                ", taxRate=" + taxRate +
                ", taxAmount=" + taxAmount +
                '}';
    }
}
