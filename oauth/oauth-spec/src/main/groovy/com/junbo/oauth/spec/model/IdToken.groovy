/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class IdToken {
    // Issuer Identifier
    String iss
    // Subject Identifier
    String sub
    // Audience
    String aud
    // Expiration time
    Long exp
    // Issued at time
    Long iat
    // Authentication time
    @JsonProperty('auth_time')
    Long authTime
    // Nonce
    String nonce
    // Authorization Code Hash
    @JsonProperty('c_hash')
    String codeHash
    // Access Token Hash
    @JsonProperty('at_hash')
    String accessTokenHash

    @JsonIgnore
    String tokenValue
}
