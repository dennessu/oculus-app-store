/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic

/**
 * UserService.
 */
@CompileStatic
interface UserService {
    Promise<UserCredentialVerifyAttempt> authenticateUser(String username, String password,
                                                          String clientId, String ipAddress, String userAgent)

    Promise<UserInfo> getUserInfo(String authorization)

    Promise<Void> sendVerifyEmail(UserId userId, ActionContextWrapper contextWrapper)

    Promise<Void> sendResetPassword(UserId userId, String locale, URI baseUri)
}
