/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Tax level output for tax calculation.
 */
@CompileStatic
@XStreamAlias('TAX')
class Tax {
    @XStreamAlias('AUTHORITY_TYPE')
    String authorityType

    @XStreamAlias('AUTHORITY_NAME')
    String authorityName

    @XStreamAlias('IS_EXEMPT')
    Boolean isExempt

    @XStreamAlias('TAX_TYPE')
    String taxType

    @XStreamAlias('TAX_RATE')
    Double taxRate

    @XStreamAlias('TAX_AMOUNT')
    TaxAmount taxAmount

    @XStreamAlias('INCLUSIVE_TAX')
    String inclusiveTax


    @Override
    public String toString() {
        return "Tax{" +
                "authorityType='" + authorityType + '\'' +
                ", authorityName='" + authorityName + '\'' +
                ", isExempt=" + isExempt +
                ", taxType='" + taxType + '\'' +
                ", taxRate=" + taxRate +
                ", taxAmount=" + taxAmount +
                ", inclusiveTax=" + inclusiveTax +
                '}';
    }
}
