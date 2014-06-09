/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import groovy.transform.CompileStatic

/**
 * Invoice Level input/output for tax calculation.
 */
@CompileStatic
@XStreamAlias('INVOICE')
class Invoice {
    // input
    @XStreamAlias('INVOICE_DATE')
    String invoiceDate

    @XStreamAlias('EXTERNAL_COMPANY_ID')
    String externalCompanyId

    @XStreamAlias('COMPANY_ROLE')
    String companyRole

    @XStreamAlias('COMPANY_NAME')
    String companyName

    @XStreamAlias('CURRENCY_CODE')
    String currencyCode

    @XStreamImplicit(itemFieldName='LINE')
    List<Line> line

    @XStreamImplicit(itemFieldName='USER_ELEMENT')
    List<UserElement> userElement

    @XStreamAlias('INVOICE_NUMBER')
    String invoiceNumber

    @XStreamAlias('IS_AUDITED')
    Boolean isAudited

    @XStreamAlias('ORIGINAL_INVOICE_DATE')
    Date originalInvoiceDate

    @XStreamAlias('ORIGINAL_INVOICE_NUMBER')
    String originalInvoiceNumber

    @XStreamAlias('NATURE_OF_TRANSACTION_CODE')
    String natureOfTransactionCode

    @XStreamAlias('BILL_TO')
    SabrixAddress billTo

    @XStreamAlias('SHIP_TO')
    SabrixAddress shipTo

    @XStreamAlias('SHIP_FROM')
    SabrixAddress shipFrom
    // end of input

    // output
    @XStreamAlias('REQUEST_STATUS')
    RequestStatus requestStatus

    @XStreamAlias('TOTAL_TAX_AMOUNT')
    Double totalTaxAmount

    @XStreamImplicit(itemFieldName='MESSAGE')
    List<Message> message

    @XStreamAlias('CALCULATION_DIRECTION')
    String calculationDirection
    // end of output

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceDate='" + invoiceDate + '\'' +
                ", externalCompanyId='" + externalCompanyId + '\'' +
                ", companyRole='" + companyRole + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", line=" + line +
                ", userElement=" + userElement +
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
                ", calculationDirection='" + calculationDirection + '\'' +
                '}';
    }
}
