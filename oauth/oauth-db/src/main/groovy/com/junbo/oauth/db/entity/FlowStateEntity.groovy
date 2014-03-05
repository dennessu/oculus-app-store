/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.entity

import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.OAuthInfo
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class FlowStateEntity {
    String id
    OAuthInfo oAuthInfo
    LoginState loginState
    String redirectUri
    Date expiredBy
}
