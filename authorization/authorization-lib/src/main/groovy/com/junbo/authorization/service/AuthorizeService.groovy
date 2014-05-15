/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service

import com.junbo.authorization.AuthorizeCallback
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * AuthorizeService.
 */
@CompileStatic
interface AuthorizeService {
    Promise<Set<String>> authorize(AuthorizeCallback callback)

    public <T> Promise<T> authorizeAndThen(AuthorizeCallback callback, Closure<Promise<T>> closure)

    public <T> Promise<T> authorizeAndThen(AuthorizeCallback callback, Promise.Func0<Promise<T>> closure)
}
