/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.test

import com.junbo.authorization.model.AccessToken
import com.junbo.authorization.model.AuthorizeContext
import groovy.transform.CompileStatic

/**
 * EntityService.
 */
@CompileStatic
interface EntityService {
    Entity get(Long id, AccessToken accessToken, AuthorizeContext context)
}
