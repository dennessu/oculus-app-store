/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service

import com.junbo.authorization.AuthorizeCallback
import groovy.transform.CompileStatic

/**
 * AuthorizeService.
 */
@CompileStatic
interface AuthorizeService {
    Set<String> getClaims(AuthorizeCallback callback)
    Boolean getAuthorizeEnabled()
}
