package com.goshop.catalog.rest.resolver

import org.springframework.data.domain.Pageable

/**
 * Created by kevingu on 11/28/13.
 */
interface PageableResolver {

    boolean canResolve(Class<?> entityClass)

    Pageable resolve(Class<?> entityClass, Map<String, Object> options)
}