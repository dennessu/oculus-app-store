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
class TaxLine {
    // Line item identifier
    @JsonProperty('LineNo')
    String lineNo
    // The tax code used in calculating tax
    @JsonProperty('TaxCode')
    String taxCode
    // Flag indicating item was taxable
    @JsonProperty('Taxability')
    Boolean taxability
    // The amount that is taxable
    @JsonProperty('Taxable')
    double taxable
    // Effective tax rate
    @JsonProperty('Rate')
    double rate
    // Tax amount
    @JsonProperty('Tax')
    double tax
    // Discount amount
    @JsonProperty('Discount')
    double discount
    // Amount of tax calculated
    @JsonProperty('TaxCalculated')
    double taxCalculated
    // Exempt amount
    @JsonProperty('Exemption')
    double exemption
    // Tax calculation details for each jurisdiction per line.
    // Returned for detail level Tax.
    @JsonProperty('TaxDetails')
    TaxDetail[] taxDetails
    // The boundary level used to calculate tax: determined by the provided addresses
    @JsonProperty('BoundaryLevel')
    String boundaryLevel
}
