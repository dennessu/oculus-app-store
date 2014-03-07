/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.api.endpoint

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.service.UserService
import com.junbo.oauth.spec.endpoint.UserInfoEndpoint
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.HttpHeaders

/**
 * Javadoc.
 */
@Component
@CompileStatic
@Scope('prototype')
class UserInfoEndpointImpl implements UserInfoEndpoint {
    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Override
    Promise<UserInfo> getUserInfo(HttpHeaders httpHeaders) {
        return Promise.pure(userService.getUserInfo(httpHeaders.requestHeaders))
    }
}
