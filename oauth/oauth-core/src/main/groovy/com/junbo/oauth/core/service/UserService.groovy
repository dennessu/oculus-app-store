/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic

/**
 * UserService.
 */
@CompileStatic
interface UserService {
    Promise<UserCredentialVerifyAttempt> authenticateUser(String username, String type, String password,
                                                          String clientId, String ipAddress, String userAgent)

    Promise<UserInfo> getUserInfo(String authorization)

    Promise<UserId> getUserIdByUserEmail(String userEmail)

    Promise<UserId> getUserIdByUsername(String username)

    Promise<UserCredential> getUserCredential(UserId userId)

    Promise<String> getUserEmailByUserId(UserId userId)

    Promise<String> sendWelcomeEmail(UserId userId, ActionContextWrapper contextWrapper)

    Promise<String> sendVerifyEmail(UserId userId, ActionContextWrapper contextWrapper)

    Promise<String> sendVerifyEmail(UserId userId, String locale, String country, Boolean welcome)

    Promise<String> sendVerifyEmail(UserId userId, String locale, String country, UserPersonalInfoId targetEmailId, Boolean welcome)

    Promise<String> sendResetPassword(UserId userId, ActionContextWrapper contextWrapper)

    Promise<String> sendResetPassword(UserId userId, String locale, String country)

    Promise<String> buildResponseLink(EmailVerifyCode emailVerifyCode)

    Promise<List<String>> getResetPasswordLinks(String username, String email, String locale, String country)

    Promise<User> getUser(UserId userId)
}
