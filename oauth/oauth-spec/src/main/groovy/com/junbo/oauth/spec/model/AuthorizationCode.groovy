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
class AuthorizationCode extends ExpirableToken {
    String code
    Long userId
    String clientId
    Set<String> scopes
    String nonce
    String redirectUri
    Date lastAuthDate
}
