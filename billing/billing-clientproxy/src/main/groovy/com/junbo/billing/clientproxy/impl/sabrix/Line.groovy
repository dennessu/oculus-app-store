/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import groovy.transform.CompileStatic

/**
 * Line level input/output for tax calculation.
 */
@CompileStatic
@XStreamAlias("LINE")
class Line {
    @XStreamAsAttribute
    @XStreamAlias('ID')
    Integer id

    @XStreamAlias('BILL_TO')
    SabrixAddress billTo

    @XStreamAlias('SHIP_TO')
    SabrixAddress shipTo

    @XStreamAlias('SHIP_FROM')
    SabrixAddress shipFrom

    @XStreamAlias('TRANSACTION_TYPE')
    String transactionType

    @XStreamAlias('PRODUCT_CODE')
    String productCode

    @XStreamAlias('GROSS_AMOUNT')
    Double grossAmount

    @XStreamAlias('LINE_NUMBER')
    Integer lineNumber

    @XStreamAlias('IS_EXEMPT')
    Exempt isExempt

    @XStreamAlias('REGISTRATIONS')
    Registrations registrations

    @XStreamAlias('TOTAL_TAX_AMOUNT')
    Double totalTaxAmount

    @XStreamImplicit(itemFieldName="TAX")
    List<Tax> tax

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", billTo=" + billTo +
                ", shipTo=" + shipTo +
                ", shipFrom=" + shipFrom +
                ", transactionType='" + transactionType + '\'' +
                ", productCode='" + productCode + '\'' +
                ", grossAmount=" + grossAmount +
                ", lineNumber=" + lineNumber +
                ", isExempt=" + isExempt +
                ", registrations=" + registrations +
                ", totalTaxAmount=" + totalTaxAmount +
                ", tax=" + tax +
                '}';
    }
}
