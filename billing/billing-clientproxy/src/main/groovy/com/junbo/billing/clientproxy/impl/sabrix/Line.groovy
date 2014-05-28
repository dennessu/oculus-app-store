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
 * Line level input/output for tax calculation.
 */
@CompileStatic
@XStreamAlias("LINE")
class Line {
    @XStreamAsAttribute
    @XStreamAlias('ID')
    Integer id

    @XStreamAsAttribute
    @XStreamAlias('BILL_TO')
    SabrixAddress billTo

    @XStreamAsAttribute
    @XStreamAlias('SHIP_TO')
    SabrixAddress shipTo

    @XStreamAsAttribute
    @XStreamAlias('SHIP_FROM')
    SabrixAddress shipFrom

    @XStreamAsAttribute
    @XStreamAlias('TRANSACTION_TYPE')
    String transactionType

    @XStreamAsAttribute
    @XStreamAlias('PRODUCT_CODE')
    String productCode

    @XStreamAsAttribute
    @XStreamAlias('GROSS_AMOUNT')
    Double grossAmount

    @XStreamAsAttribute
    @XStreamAlias('LINE_NUMBER')
    Integer lineNumber

    @XStreamAsAttribute
    @XStreamAlias('IS_EXEMPT')
    Exempt isExempt

    @XStreamAsAttribute
    @XStreamAlias('REGISTRATIONS')
    Registrations registrations

    @XStreamAsAttribute
    @XStreamAlias('TOTAL_TAX_AMOUNT')
    Double totalTaxAmount

    @XStreamAsAttribute
    @XStreamAlias('TAX')
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
