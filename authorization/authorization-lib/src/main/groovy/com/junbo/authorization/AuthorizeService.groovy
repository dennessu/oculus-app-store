/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * AuthorizeService.
 */
@CompileStatic
interface AuthorizeService {
    Set<String> authorize(AuthorizeCallback callback)
}
