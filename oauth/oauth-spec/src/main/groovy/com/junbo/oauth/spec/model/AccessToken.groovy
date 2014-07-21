/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class AccessToken extends ExpirableToken {
    String tokenValue
    String clientId
    Set<String> scopes
    Long userId
    String refreshTokenValue
    String ipAddress

    @Override
    String getId() {
        return tokenValue
    }

    @Override
    void setId(String id) {
        this.tokenValue = id
    }
}
