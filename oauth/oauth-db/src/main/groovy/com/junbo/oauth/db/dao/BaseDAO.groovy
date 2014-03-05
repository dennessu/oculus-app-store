/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao

import groovy.transform.CompileStatic

/**
 * Javadoc.
 * @param < T >
 * @param < K >
 */
@CompileStatic
interface BaseDAO<T, K> {
    T save(T entity)

    T get(K id)

    T update(T t)

    void delete(K id)
}
