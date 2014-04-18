/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.junbo.common.jackson.annotation.OfferId
import com.junbo.common.jackson.annotation.WalletId
import groovy.transform.CompileStatic

/**
 * Transaction Entity. Only for response.
 */
@CompileStatic
@JsonPropertyOrder(['transactionId', 'type', 'amount', 'offerId', 'createdBy', 'createdTime'])
@JsonInclude(JsonInclude.Include.NON_NULL)
class Transaction {
    Long transactionId
    @WalletId
    Long walletId
    String type
    BigDecimal amount
    @OfferId
    @JsonProperty('offer')
    Long offerId
    Date createdTime
    String createdBy
}
