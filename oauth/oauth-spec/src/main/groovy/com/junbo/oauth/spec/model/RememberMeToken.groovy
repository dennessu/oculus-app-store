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
class RememberMeToken extends ExpirableToken {
    String tokenValue

    Long userId

    String stolen

    Date lastAuthDate

    @Override
    String getId() {
        return tokenValue
    }

    @Override
    void setId(String id) {
        this.tokenValue = id
    }
}
