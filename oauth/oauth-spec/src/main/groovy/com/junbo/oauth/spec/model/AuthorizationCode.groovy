/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.junbo.common.cloudant.json.annotations.CloudantIgnore
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class AuthorizationCode extends ExpirableToken {
    @CloudantIgnore
    String code

    @JsonIgnore
    String hashedCode
    Long userId
    String clientId
    Set<String> scopes
    String nonce
    String redirectUri
    Date lastAuthDate
    String loginStateHash

    @Override
    String getId() {
        return hashedCode
    }

    @Override
    void setId(String id) {
        this.hashedCode = id
    }
}
