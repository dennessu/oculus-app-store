/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId

import javax.ws.rs.QueryParam

/**
 * AgeCheck.
 */
class AgeCheck {
    @JsonProperty('status')
    Status status

    @JsonIgnore
    @QueryParam('country')
    String country

    @JsonIgnore
    @QueryParam('userId')
    UserId userId

    @JsonIgnore
    @QueryParam('dob')
    Date dob

    @JsonIgnore
    @QueryParam('offerId')
    List<OfferId> offerIds
}
