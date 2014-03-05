package com.goshop.catalog.rest.resolver

import org.springframework.data.domain.Sort
/**
 * Created by kevingu on 11/28/13.
 */
interface SortResolver {

    boolean canResolve(Class<?> entityClass)

    Sort resolve(Class<?> entityClass, Map<String, Object> options)
}