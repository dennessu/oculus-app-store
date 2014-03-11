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
    @JsonProperty('LineNo')
    String lineNo
    @JsonProperty('TaxCode')
    String taxCode
    @JsonProperty('Taxability')
    Boolean taxability
    @JsonProperty('Taxable')
    double taxable
    @JsonProperty('Rate')
    double rate
    @JsonProperty('Tax')
    double tax
    @JsonProperty('Discount')
    double discount
    @JsonProperty('TaxCalculated')
    double taxCalculated
    @JsonProperty('Exemption')
    double exemption
    @JsonProperty('TaxDetails')
    TaxDetail[] taxDetails
    @JsonProperty('BoundaryLevel')
    String boundaryLevel
}
