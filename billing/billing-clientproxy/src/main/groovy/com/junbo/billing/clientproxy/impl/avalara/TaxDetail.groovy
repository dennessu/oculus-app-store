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
    @JsonProperty('Rate')
    double rate
    @JsonProperty('Tax')
    double tax
    @JsonProperty('Taxable')
    double taxable
    @JsonProperty('Country')
    String country
    @JsonProperty('Region')
    String region
    @JsonProperty('JurisType')
    String jurisType
    @JsonProperty('JurisName')
    String jurisName
    @JsonProperty('TaxName')
    String taxName
}
