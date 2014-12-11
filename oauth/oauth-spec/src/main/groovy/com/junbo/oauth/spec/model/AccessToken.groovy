/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class AccessToken extends ExpirableToken {
    @CloudantIgnore
    String tokenValue

    @JsonIgnore
    String hashedTokenValue

    String clientId
    Set<String> scopes
    Long userId
    String refreshTokenValue
    String ipAddress
    Boolean debugEnabled
    String loginStateHash

    @Override
    String getId() {
        return hashedTokenValue
    }

    @Override
    void setId(String id) {
        this.hashedTokenValue = id
    }
}
