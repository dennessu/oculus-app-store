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
class OAuthInfo {
    Set<String> scopes
    String redirectUri
    Set<Prompt> prompts
    Display display
    Set<ResponseType> responseTypes
    GrantType grantType
    String nonce
    String state
    Long maxAge
}
