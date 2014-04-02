/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic

/**
 * MockTokenInfoEndpoint.
 */
@CompileStatic
class MockTokenInfoEndpoint implements TokenInfoEndpoint {
    Promise<TokenInfo> getTokenInfo(String tokenValue) {
        return Promise.pure(new TokenInfo(sub: new UserId(Long.parseLong(tokenValue)), clientId: 'client', scopes: 'entity identity api.info'))
    }
}
