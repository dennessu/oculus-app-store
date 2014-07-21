/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic
import org.springframework.util.Assert
/**
 * Consent.
 */
@CompileStatic
class Consent extends ResourceMeta<String> {
    Long userId
    String clientId
    Set<String> scopes

    @Override
    String getId() {
        return "$userId#$clientId"
    }

    @Override
    void setId(String id) {
        String[] tokens = id.split('#')
        Assert.isTrue(tokens.length == 2)
        userId = Long.parseLong(tokens[0])
        clientId = tokens[1]
    }
}
