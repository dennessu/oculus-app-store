/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.oauth.spec.model.TokenInfo
import groovy.transform.CompileStatic

/**
 * AuthorizeCallback.
 */
@CompileStatic
interface AuthorizeCallback<T> {
    String getApiName()

    void setTokenInfo(TokenInfo tokenInfo)

    boolean isInGroup(String groupName)

    boolean hasRoleAssignment(String roleName)

    AuthorizeCallback<T> initialize(Map<String, Object> context)

    T postFilter()
}
