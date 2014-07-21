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

/**
 * Default {@link com.junbo.oauth.spec.endpoint.UserInfoEndpoint} implementation.
 * @author Zhanxin Yang
 * @see com.junbo.oauth.spec.endpoint.UserInfoEndpoint
 */
@Component
@CompileStatic
class UserInfoEndpointImpl implements UserInfoEndpoint {
    /**
     * The UserService to handle the user related logic.
     */
    private UserService userService

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    /**
     * Endpoint to retrieve the user information.
     * @param authorization The http header Authorization, contains the access token in Bearer format.
     * @return The user information of the access token's user id.
     */
    @Override
    Promise<UserInfo> getUserInfo(String authorization) {
        return userService.getUserInfo(authorization)
    }
}
