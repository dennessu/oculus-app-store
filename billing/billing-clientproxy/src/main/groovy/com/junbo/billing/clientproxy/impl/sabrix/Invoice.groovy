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

    @XStreamAlias('HOST_SYSTEM')
    String hostSystem

    @XStreamAlias('CALLING_SYSTEM_NUMBER')
    String callingSystemNumber

    @XStreamAlias('UNIQUE_INVOICE_NUMBER')
    String uniqueInvoiceNumber

    @XStreamAlias('DELIVERY_TERMS')
    String deliveryTerms

    @XStreamAlias('EXTERNAL_COMPANY_ID')
    String externalCompanyId

    @XStreamAlias('INVOICE_NUMBER')
    String invoiceNumber

    @XStreamAlias('CALCULATION_DIRECTION')
    String calculationDirection

    @XStreamAlias('COMPANY_ROLE')
    String companyRole

    @XStreamAlias('CURRENCY_CODE')
    String currencyCode

    @XStreamImplicit(itemFieldName='LINE')
    List<Line> line

    @XStreamImplicit(itemFieldName='USER_ELEMENT')
    List<UserElement> userElement

    @XStreamAlias('IS_AUDITED')
    Boolean isAudited

    @XStreamAlias('ORIGINAL_INVOICE_DATE')
    String originalInvoiceDate

    // duplicate ORIGINAL_INVOICE_NUMBER: known issue of Sabrix
    @XStreamImplicit(itemFieldName='ORIGINAL_INVOICE_NUMBER')
    List<String> originalInvoiceNumber

    @XStreamAlias('SELLER_PRIMARY')
    SabrixAddress sellerPrimary

    @XStreamAlias('BUYER_PRIMARY')
    SabrixAddress buyerPrimary

    @XStreamAlias('BILL_TO')
    SabrixAddress billTo

    @XStreamAlias('SHIP_TO')
    SabrixAddress shipTo

    @XStreamAlias('SHIP_FROM')
    SabrixAddress shipFrom

    @XStreamAlias('CUSTOMER_NAME')
    String customerName

    @XStreamAlias('CUSTOMER_NUMBER')
    String customerNumber

    @XStreamAlias('EXEMPT_REASON')
    ExemptReason exemptReason

    @XStreamAlias('EXEMPT_CERTIFICATE')
    ExemptCertificate exemptCertificate

    @XStreamAlias('IS_EXEMPT')
    IsExempt isExempt
    // end of input

    // output
    @XStreamAlias('REQUEST_STATUS')
    RequestStatus requestStatus

    @XStreamAlias('TOTAL_TAX_AMOUNT')
    Double totalTaxAmount

    @XStreamImplicit(itemFieldName='MESSAGE')
    List<Message> message
    // end of output


    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceDate='" + invoiceDate + '\'' +
                ", hostSystem='" + hostSystem + '\'' +
                ", callingSystemNumber='" + callingSystemNumber + '\'' +
                ", uniqueInvoiceNumber='" + uniqueInvoiceNumber + '\'' +
                ", deliveryTerms='" + deliveryTerms + '\'' +
                ", externalCompanyId='" + externalCompanyId + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", calculationDirection='" + calculationDirection + '\'' +
                ", companyRole='" + companyRole + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", line=" + line +
                ", userElement=" + userElement +
                ", isAudited=" + isAudited +
                ", originalInvoiceDate='" + originalInvoiceDate + '\'' +
                ", originalInvoiceNumber='" + originalInvoiceNumber + '\'' +
                ", billTo=" + billTo +
                ", shipTo=" + shipTo +
                ", shipFrom=" + shipFrom +
                ", customerName='" + customerName + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", requestStatus=" + requestStatus +
                ", totalTaxAmount=" + totalTaxAmount +
                ", message=" + message +
                '}';
    }
}
