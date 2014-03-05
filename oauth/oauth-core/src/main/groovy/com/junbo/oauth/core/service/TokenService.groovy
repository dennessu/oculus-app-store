/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic

/**
 * TokenService.
 */
@CompileStatic
interface TokenService {
    void grantAccessToken(ServiceContext context)

    TokenInfo getAccessTokenInfo(ServiceContext context)
}
