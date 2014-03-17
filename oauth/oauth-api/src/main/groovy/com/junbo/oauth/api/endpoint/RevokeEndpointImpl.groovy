/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.RevokeEndpoint
import groovy.transform.CompileStatic

import javax.ws.rs.core.Response

/**
 * RevokeEndpointImpl.
 */
@CompileStatic
class RevokeEndpointImpl implements RevokeEndpoint {
    @Override
    Promise<Response> revoke(String authorization, String token, String tokenTypeHint) {
        return null
    }
}
