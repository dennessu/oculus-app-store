/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara

/**
 * Created by LinYi on 14-3-10.
 */
class GetTaxResponse {
    String docCode
    Date docDate
    Date timestamp
    double totalAmount
    double totalDiscount
    double totalExemption
    double totalTaxable
    double totalTax
    double totalTaxCalculated
    Date taxDate
    TaxLine[] taxLines
    TaxLine[] taxSummary
    TaxAddress[] taxAddresses
    SeverityLevel resultCode
    ResponseMessage[] messages
}
