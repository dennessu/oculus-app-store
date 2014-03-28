/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.model

import groovy.transform.CompileStatic

/**
 * AuthorizeContext.
 */
@CompileStatic
class AuthorizeContext {
    static final ThreadLocal<Set<String>> CLAIMS = new ThreadLocal<>()
    static Boolean authorizedEnabled
    static Boolean hasClaim(String claim) {
        if (authorizedEnabled) {
            return CLAIMS.get().contains(claim)
        }
        return true
    }
}
