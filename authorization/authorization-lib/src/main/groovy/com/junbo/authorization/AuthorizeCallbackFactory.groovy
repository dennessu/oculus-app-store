/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import groovy.transform.CompileStatic

/**
 * AuthorizeCallbackFactory.
 */
@CompileStatic
interface AuthorizeCallbackFactory<T> {

    AuthorizeCallback<T> create(T entity)
}
