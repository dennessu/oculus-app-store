/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface LoginStateRepository {
    LoginState get(String id)

    LoginState save(LoginState loginState)

    void remove(String id)

    void removeByHash(String id)
}
