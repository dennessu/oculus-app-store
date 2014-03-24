/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.UserInfo
import groovy.transform.CompileStatic

/**
 * UserService.
 */
@CompileStatic
interface UserService {
    Promise<User> authenticateUser(String username, String password)

    UserInfo getUserInfo(String authorization)
}
