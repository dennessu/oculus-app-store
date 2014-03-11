/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.RememberMeToken
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface RememberMeTokenRepository {
    void save(RememberMeToken rememberMeToken)

    RememberMeToken get(String tokenValue)

    RememberMeToken getAndRemove(String tokenValue)
}
