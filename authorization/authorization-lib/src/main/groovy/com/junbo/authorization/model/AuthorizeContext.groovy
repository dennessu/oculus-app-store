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
    Set<String> claims = []
    Map<String, Object> context
}
