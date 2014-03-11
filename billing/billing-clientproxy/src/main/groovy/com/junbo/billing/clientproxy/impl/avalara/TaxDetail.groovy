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
class TaxDetail {
    // Effective tax rate for tax jurisdiction
    @JsonProperty('Rate')
    double rate
    // Tax amount
    @JsonProperty('Tax')
    double tax
    // Taxable amount on the line
    @JsonProperty('Taxable')
    double taxable
    // Country of tax jurisdiction
    @JsonProperty('Country')
    String country
    // Region of tax jurisdiction
    @JsonProperty('Region')
    String region
    // Regional type of tax jurisdiction
    @JsonProperty('JurisType')
    String jurisType
    // Name of tax jurisdiction
    @JsonProperty('JurisName')
    String jurisName
    // Tax name
    @JsonProperty('TaxName')
    String taxName
}
