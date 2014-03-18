/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.junbo.common.jackson.annotation.OfferId
import groovy.transform.CompileStatic

/**
 * credit request.
 */
@CompileStatic
class CreditRequest {
    UUID trackingUuid
    @OfferId
    @JsonProperty('offer')
    Long offerId
    BigDecimal amount
    String type
    Date expirationDate
}
