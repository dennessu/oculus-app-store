/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class AccessTokenResponse {
    @JsonProperty('access_token')
    String accessToken

    @JsonProperty('token_type')
    String tokenType

    @JsonProperty('expires_in')
    Long expiresIn

    @JsonProperty('refresh_token')
    String refreshToken

    @JsonProperty('id_token')
    String idToken
}
