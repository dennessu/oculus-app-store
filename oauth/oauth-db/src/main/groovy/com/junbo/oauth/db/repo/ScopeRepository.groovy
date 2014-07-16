/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic

/**
 * ScopeRepository.
 */
@CompileStatic
interface ScopeRepository {
    Scope getScope(String name)

    Scope saveScope(Scope scope)

    Scope updateScope(Scope scope, Scope oldScope)

    void deleteScope(Scope scope)
}
