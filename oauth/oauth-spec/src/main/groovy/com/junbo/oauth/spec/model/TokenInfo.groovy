/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class TokenInfo {
    UserId sub

    @JsonProperty('expires_in')
    Long expiresIn

    String scopes

    @JsonProperty('client_id')
    String clientId

    @JsonProperty('ip_address')
    String ipAddress
}
