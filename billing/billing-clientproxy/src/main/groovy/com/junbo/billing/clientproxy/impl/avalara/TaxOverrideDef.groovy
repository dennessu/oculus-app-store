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
class TaxOverrideDef {
    @JsonProperty('TaxOverrideType')
    String taxOverrideType
    @JsonProperty('Reason')
    String reason
    @JsonProperty('TaxAmount')
    String taxAmount
    @JsonProperty('TaxDate')
    String taxDate
}
