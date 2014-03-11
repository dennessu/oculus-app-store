/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by LinYi on 14-3-10.
 */
class GetTaxResponse {
    // The document code, if not supplied in the request the returned value is a GUID.
    @JsonProperty('DocCode')
    String docCode
    // Date of invoice, sales order, purchase order, etc.
    @JsonProperty('DocDate')
    Date docDate
    // Server timestamp of request.
    @JsonProperty('Timestamp')
    Date timestamp
    // Sum of all line Amount values.
    @JsonProperty('TotalAmount')
    double totalAmount
    // Sum of all TaxLine discount amounts.
    @JsonProperty('TotalDiscount')
    double totalDiscount
    // Total exemption amount.
    @JsonProperty('TotalExemption')
    double totalExemption
    // Total taxable amount.
    @JsonProperty('TotalTaxable')
    double totalTaxable
    // Sum of all TaxLine tax amounts.
    @JsonProperty('TotalTax')
    double totalTax
    // TotalTaxCalculated indicates the total tax calculated by AvaTax.
    // This is usually the same as the TotalTax, except when a tax override amount is specified.
    // This is for informational purposes.
    // The TotalTax will still be used for reporting.
    @JsonProperty('TotalTaxCalculated')
    double totalTaxCalculated
    // Date used to assess tax rates and jurisdictions.
    @JsonProperty('TaxDate')
    Date taxDate
    // Tax calculation details for each item line.
    // Returned for detail levels Line and Tax.
    @JsonProperty('TaxLines')
    TaxLine[] taxLines
    // Summary of the jurisdiction details for all item lines.
    // Returned for detail levels Summary and Line.
    @JsonProperty('TaxSummary')
    TaxLine[] taxSummary
    // Addresses used in tax calculations.
    // Returned for detail levels Line and Tax.
    @JsonProperty('TaxAddresses')
    TaxAddress[] taxAddresses
    @JsonProperty('ResultCode')
    SeverityLevel resultCode
    @JsonProperty('Messages')
    ResponseMessage[] messages
}
