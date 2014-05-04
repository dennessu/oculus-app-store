/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

import java.sql.Date

/**
 * GetTaxRequest
 */
@CompileStatic
class GetTaxRequest {

    // Required for tax calculation: DocDate, CustomerCode, Addresses, Lines

    // Date of invoice, sales order, purchase order, etc.
    // Must be valid YYYY-MM-DD format
    @JsonProperty('DocDate')
    Date docDate
    // Client application customer reference code.
    @JsonProperty('CustomerCode')
    String customerCode
    // Address, at least one required
    // Should represent all origin and destination addresses which will be associated with individual lines.
    @JsonProperty('Addresses')
    AvalaraAddress[] addresses
    // Line, at least one required
    // Document line array. There is a limit of 1000 lines per document.
    @JsonProperty('Lines')
    Line[] lines

    // Best Practice for tax calculation: DocCode, DocType, CompanyCode, Commit, DetailLevel, Client

    // Client application identifier describing this tax transaction (i.e. invoice number, sales order number, etc.)
    @JsonProperty('DocCode')
    String docCode
    // The document types supported include
    // SalesOrder, SalesInvoice, ReturnOrder, and ReturnInvoice.
    @JsonProperty('DocType')
    DocType docType
    // Client application company reference code
    @JsonProperty('CompanyCode')
    String companyCode
    // Default is false.
    // Setting this value to true will prevent further document status changes, except voiding with CancelTax.
    @JsonProperty('Commit')
    Boolean commit
    // Specifies the level of tax detail to return
    @JsonProperty('DetailLevel')
    DetailLevel detailLevel
    // An identifier of software client generating the API call.
    @JsonProperty('Client')
    String client

    // Use where appropriate to the situation: CustomerUsageType, ExemptionNo, Discount, TaxOverride

    // Client application customer or usage type.
    // CustomerUsageType determines the exempt status of the transaction
    // based on the exemption tax rules for the jurisdictions involved.
    // CustomerUsageType may also be set at the line item level.Can also be referred to as Entity/Use Code.
    @JsonProperty('CustomerUsageType')
    String customerUsageType
    // Exemption number used for this transaction
    @JsonProperty('ExemptionNo')
    String exemptionNo
    // Document level discount amount applied to transaction
    @JsonProperty('Discount')
    BigDecimal discount
    // Accommodates supplying a tax value calculated by an external source.
    @JsonProperty('TaxOverride')
    TaxOverrideDef taxOverride
    // The buyer's VAT id.
    // Using this value will force VAT rules to be considered for the transaction.
    // This may be set on the document or the line.
    @JsonProperty('BusinessIdentificationNo')
    String businessIdentificationNo

    // Optional: PurchaseOrderNo, PaymentDate, ReferenceCode, PosLaneCode

    // Buyer��s purchase order identifier.
    // PurchaseOrderNo is required for single use exemption certificates
    // to match the order and invoice with the certificate.
    @JsonProperty('PurchaseOrderNo')
    String purchaseOrderNo
    // Value stored with SalesInvoice DocType that is submitter dependent.
    @JsonProperty('ReferenceCode')
    String referenceCode
    // Permits a Point of Sale application to record the unique code / ID / number
    // associated with the terminal processing a sale.
    @JsonProperty('PosLaneCode')
    String posLaneCode

    @Override
    String toString() {
        return 'GetTaxRequest{' +
                'docDate=' + docDate +
                ", customerCode='" + customerCode + '\'' +
                ', addresses=' + Arrays.toString(addresses) +
                ', lines=' + Arrays.toString(lines) +
                ", docCode='" + docCode + '\'' +
                ', docType=' + docType +
                ", companyCode='" + companyCode + '\'' +
                ', commit=' + commit +
                ', detailLevel=' + detailLevel +
                ", client='" + client + '\'' +
                ", customerUsageType='" + customerUsageType + '\'' +
                ", exemptionNo='" + exemptionNo + '\'' +
                ', discount=' + discount +
                ', taxOverride=' + taxOverride +
                ", businessIdentificationNo='" + businessIdentificationNo + '\'' +
                ", purchaseOrderNo='" + purchaseOrderNo + '\'' +
                ", referenceCode='" + referenceCode + '\'' +
                ", posLaneCode='" + posLaneCode + '\'' +
                '}'
    }
}
