/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.model

import groovy.transform.CompileStatic

/**
 * AccessToken.
 */
@CompileStatic
class AccessToken {
    String tokenValue
    Long userId
    Set<Scope> scopes
}
