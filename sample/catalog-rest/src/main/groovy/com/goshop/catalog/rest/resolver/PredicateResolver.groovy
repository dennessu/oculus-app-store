package com.goshop.catalog.rest.resolver

import com.mysema.query.types.Predicate

/**
 * Created by kevingu on 11/28/13.
 */
interface PredicateResolver {

    boolean canResolve(Class<?> entityClass)

    Predicate resolve(Class<?> entityClass, Map<String, Object> options)
}