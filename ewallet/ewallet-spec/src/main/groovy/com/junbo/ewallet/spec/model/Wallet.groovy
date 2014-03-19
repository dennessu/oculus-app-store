/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.junbo.common.jackson.annotation.UserId
import com.junbo.common.jackson.annotation.WalletId
import groovy.transform.CompileStatic
/**
 * Wallet Entity.
 */
@CompileStatic
@JsonPropertyOrder(['walletId', 'userId', 'type', 'currency', 'balance', 'status', 'transactions'])
@JsonInclude(JsonInclude.Include.NON_NULL)
class Wallet {
    @WalletId
    @JsonProperty('self')
    Long walletId
    UUID trackingUuid
    @UserId
    @JsonProperty('user')
    Long userId
    String type
    String status
    String currency
    BigDecimal balance
    List<Transaction> transactions

    @JsonIgnore
    UUID getTrackingUuid() {
        return trackingUuid
    }

    @JsonProperty
    void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid
    }
}
