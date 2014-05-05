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
import com.wordnik.swagger.annotations.ApiModelProperty
import groovy.transform.CompileStatic

import javax.ws.rs.QueryParam

/**
 * AgeCheck.
 */
@CompileStatic
class AgeCheck {
    @ApiModelProperty(position = 1, required = true, value = '(PASSED,BLOCKED,BANNED)\
                 PASSED represent pass the age gate, BLOCKED represent need to trigger the age verify, \
                 BANNED represent the age gate failed.')
    @JsonProperty('status')
    Status status

    @ApiModelProperty(required = true, value = 'Country of age gate.')
    @JsonIgnore
    @QueryParam('country')
    String country

    @ApiModelProperty(required = false, value = 'Get user dob by userId.(optional if dob is specified, \
                                            if userId and dob are specified both, ignore userId)')
    @JsonIgnore
    @QueryParam('userId')
    UserId userId

    @ApiModelProperty(required = false, value = 'Calculate user age by dob directly, \
                                then compare with offer restriction info.(optional, if userId is specified. )')
    @JsonIgnore
    @QueryParam('dob')
    Date dob

    @ApiModelProperty(required = true, value = 'Get offer info by offerId, then calculate the offer \
                                                    restriction info(like 16+, 18+).')
    @JsonIgnore
    @QueryParam('offerId')
    List<OfferId> offerIds
}
