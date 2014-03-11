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
    @JsonProperty('DocCode')
    String docCode
    @JsonProperty('DocDate')
    Date docDate
    @JsonProperty('Timestamp')
    Date timestamp
    @JsonProperty('TotalAmount')
    double totalAmount
    @JsonProperty('TotalDiscount')
    double totalDiscount
    @JsonProperty('TotalExemption')
    double totalExemption
    @JsonProperty('TotalTaxable')
    double totalTaxable
    @JsonProperty('TotalTax')
    double totalTax
    @JsonProperty('TotalTaxCalculated')
    double totalTaxCalculated
    @JsonProperty('TaxDate')
    Date taxDate
    @JsonProperty('TaxLines')
    TaxLine[] taxLines
    @JsonProperty('TaxSummary')
    TaxLine[] taxSummary
    @JsonProperty('TaxAddresses')
    TaxAddress[] taxAddresses
    @JsonProperty('ResultCode')
    SeverityLevel resultCode
    @JsonProperty('Messages')
    ResponseMessage[] messages
}
