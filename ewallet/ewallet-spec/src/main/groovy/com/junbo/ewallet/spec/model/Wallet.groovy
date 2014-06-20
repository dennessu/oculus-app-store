/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.junbo.common.jackson.annotation.UserId
import com.junbo.common.jackson.annotation.WalletId
import com.junbo.common.model.ResourceMetaForDualWrite
import groovy.transform.CompileStatic
/**
 * Wallet Entity.
 */
@CompileStatic
@JsonPropertyOrder(['id', 'userId', 'type', 'currency', 'balance', 'status'])
@JsonInclude(JsonInclude.Include.NON_NULL)
class Wallet extends ResourceMetaForDualWrite<Long> {
    @WalletId
    @JsonProperty('self')
    Long id
    @UserId
    @JsonProperty('user')
    Long userId
    String type
    String status
    String currency
    BigDecimal balance
    UUID trackingUuid
}
