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
class LoginState extends ExpirableToken {
    @CloudantIgnore
    String loginStateId

    @JsonIgnore
    String hashedId
    Long userId
    Date lastAuthDate
    String sessionId

    @Override
    String getId() {
        return hashedId
    }

    @Override
    void setId(String id) {
        this.hashedId = id
    }
}
