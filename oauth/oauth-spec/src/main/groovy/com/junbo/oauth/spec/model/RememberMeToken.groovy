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
class RememberMeToken extends ExpirableToken {
    @CloudantIgnore
    String tokenValue

    @JsonIgnore
    String hashedTokenValue

    Long userId

    String stolen

    Date lastAuthDate

    @Override
    String getId() {
        return hashedTokenValue
    }

    @Override
    void setId(String id) {
        this.hashedTokenValue = id
    }
}
