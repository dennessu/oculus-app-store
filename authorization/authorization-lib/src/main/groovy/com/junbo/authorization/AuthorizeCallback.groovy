/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.model.Role
import groovy.transform.CompileStatic

/**
 * AuthorizeCallback.
 */
@CompileStatic
interface AuthorizeCallback<T> {
    AuthorizeContext newContext(Map<String, Object> map)

    AuthorizeContext newContext(T resourceEntity)

    Boolean hasRole(Role role, AuthorizeContext context)

    T postFilter(T resourceEntity, AuthorizeContext context)
}
