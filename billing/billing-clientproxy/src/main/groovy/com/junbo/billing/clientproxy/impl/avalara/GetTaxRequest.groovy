/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import java.sql.Date

/**
 * GetTaxRequest
 */
class GetTaxRequest {

    // Required for tax calculation: DocDate, CustomerCode, Addresses, Lines

    // Date of invoice, sales order, purchase order, etc.
    // Must be valid YYYY-MM-DD format
    Date docDate
    // Client application customer reference code.
    String customerCode
    AvalaraAddress[] addresses
    com.junbo.billing.clientproxy.impl.avalara.Line[] lines

    // Best Practice for tax calculation: DocCode, DocType, CompanyCode, Commit, DetailLevel, Client

    // Client application identifier describing this tax transaction (i.e. invoice number, sales order number, etc.)
    String docCode
    // The document types supported include
    // SalesOrder, SalesInvoice, ReturnOrder, and ReturnInvoice.
    com.junbo.billing.clientproxy.impl.avalara.DocType docType
    // Client application company reference code
    String companyCode
    // Default is false.
    // Setting this value to true will prevent further document status changes, except voiding with CancelTax.
    Boolean commit
    // Specifies the level of tax detail to return
    com.junbo.billing.clientproxy.impl.avalara.DetailLevel detailLevel
    // An identifier of software client generating the API call.
    String client

    // Use where appropriate to the situation: CustomerUsageType, ExemptionNo, Discount, TaxOverride

    // Client application customer or usage type.
    // CustomerUsageType determines the exempt status of the transaction
    // based on the exemption tax rules for the jurisdictions involved.
    // CustomerUsageType may also be set at the line item level.Can also be referred to as Entity/Use Code.
    String customerUsageType
    // Exemption number used for this transaction
    String exemptionNo
    // Document level discount amount applied to transaction
    BigDecimal discount
    // Accommodates supplying a tax value calculated by an external source.
    com.junbo.billing.clientproxy.impl.avalara.TaxOverrideDef taxOverride
    // The buyer's VAT id.
    // Using this value will force VAT rules to be considered for the transaction.
    // This may be set on the document or the line.
    String businessIdentificationNo

    // Optional: PurchaseOrderNo, PaymentDate, ReferenceCode, PosLaneCode

    // Buyer¡¯s purchase order identifier.
    // PurchaseOrderNo is required for single use exemption certificates
    // to match the order and invoice with the certificate.
    String purchaseOrderNo
    // Value stored with SalesInvoice DocType that is submitter dependent.
    String referenceCode
    // Permits a Point of Sale application to record the unique code / ID / number
    // associated with the terminal processing a sale.
    String posLaneCode
}
