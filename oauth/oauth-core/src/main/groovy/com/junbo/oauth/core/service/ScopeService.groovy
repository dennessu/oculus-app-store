/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.service

import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic

/**
 * ScopeServiceImpl.
 */
@CompileStatic
interface ScopeService {
    Scope saveScope(Scope scope)

    Scope getScope(String scopeName)

    List<Scope> getScopes(String scopeNames)

    Scope updateScope(String scopeName, Scope scope)
}
