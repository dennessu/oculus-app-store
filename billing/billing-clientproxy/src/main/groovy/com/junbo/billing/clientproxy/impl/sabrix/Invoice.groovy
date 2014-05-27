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
 * Invoice Level input/output for tax calculation.
 */
@CompileStatic
@XStreamAlias("INVOICE")
class Invoice {
    // input
    @XStreamAsAttribute
    @XStreamAlias('INVOICE_DATE')
    Date invoiceDate

    @XStreamAsAttribute
    @XStreamAlias('CURRENCY_CODE')
    String currencyCode

    @XStreamAsAttribute
    @XStreamAlias('CALCULATION_DIRECTION')
    String calculationDirection

    @XStreamAsAttribute
    @XStreamAlias('LINE')
    List<Line> line

    @XStreamAsAttribute
    @XStreamAlias('INVOICE_NUBMER')
    String invoiceNumber

    @XStreamAsAttribute
    @XStreamAlias('IS_AUDITED')
    Boolean isAudited

    @XStreamAsAttribute
    @XStreamAlias('ORIGINAL_INVOICE_DATE')
    Date originalInvoiceDate

    @XStreamAsAttribute
    @XStreamAlias('ORIGINAL_INVOICE_NUMBER')
    String originalInvoiceNumber

    @XStreamAsAttribute
    @XStreamAlias('NATURE_OF_TRANSACTION_CODE')
    String natureOfTransactionCode

    @XStreamAsAttribute
    @XStreamAlias('BILL_TO')
    SabrixAddress billTo

    @XStreamAsAttribute
    @XStreamAlias('SHIP_TO')
    SabrixAddress shipTo

    @XStreamAsAttribute
    @XStreamAlias('SHIP_FROM')
    SabrixAddress shipFrom
    // end of input

    // output
    @XStreamAsAttribute
    @XStreamAlias('REQUEST_STATUS')
    RequestStatus requestStatus

    @XStreamAsAttribute
    @XStreamAlias('TOTAL_TAX_AMOUNT')
    Double totalTaxAmount

    @XStreamAsAttribute
    @XStreamAlias('MESSAGE')
    List<Message> message
    // end of output


    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceDate=" + invoiceDate +
                ", currencyCode='" + currencyCode + '\'' +
                ", calculationDirection='" + calculationDirection + '\'' +
                ", line=" + line +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", isAudited=" + isAudited +
                ", originalInvoiceDate=" + originalInvoiceDate +
                ", originalInvoiceNumber='" + originalInvoiceNumber + '\'' +
                ", natureOfTransactionCode='" + natureOfTransactionCode + '\'' +
                ", billTo=" + billTo +
                ", shipTo=" + shipTo +
                ", shipFrom=" + shipFrom +
                ", requestStatus=" + requestStatus +
                ", totalTaxAmount=" + totalTaxAmount +
                ", message=" + message +
                '}';
    }
}
