/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.authorization.model.Role
import groovy.transform.CompileStatic

/**
 * AuthorizeCallback.
 */
@CompileStatic
interface AuthorizeCallback {
    Boolean hasRole(Role role)
}
