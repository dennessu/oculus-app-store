/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

/**
 * Wallet Entity.
 */
@CompileStatic
class Wallet extends Model {
    Long walletId
    UUID trackingUuid
    @NotNull
    Long userId
    @NotNull
    String type
    @Null
    String status
    String currency
    BigDecimal balance
    @Null
    List<Transaction> comments

    @JsonIgnore
    UUID getTrackingUuid() {
        return trackingUuid
    }

    @JsonProperty
    void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid
    }
}
