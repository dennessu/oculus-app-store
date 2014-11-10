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
class RefreshToken extends ExpirableToken {
    @CloudantIgnore
    String tokenValue

    @JsonIgnore
    String hashedTokenValue

    String clientId
    Long userId
    AccessToken accessToken
    String salt

    Boolean stolen

    @CloudantIgnore
    String newTokenValue

    @JsonIgnore
    String encryptedNewTokenValue

    @Override
    String getId() {
        return hashedTokenValue
    }

    @Override
    void setId(String id) {
        this.hashedTokenValue = id
    }
}
