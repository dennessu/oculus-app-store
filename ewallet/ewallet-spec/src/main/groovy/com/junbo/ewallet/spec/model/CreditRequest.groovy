/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model
import com.fasterxml.jackson.annotation.JsonProperty
import com.junbo.common.jackson.annotation.OfferId
import com.junbo.common.jackson.annotation.UserId
import com.junbo.common.jackson.annotation.WalletId
import groovy.transform.CompileStatic
/**
 * credit request.
 */
@CompileStatic
class CreditRequest {
    UUID trackingUuid
    @WalletId
    @JsonProperty('wallet')
    Long walletId
    @UserId
    @JsonProperty('user')
    Long userId
    String currency
    @OfferId
    @JsonProperty('offer')
    Long offerId
    BigDecimal amount
    String type
    Date expirationDate
}
