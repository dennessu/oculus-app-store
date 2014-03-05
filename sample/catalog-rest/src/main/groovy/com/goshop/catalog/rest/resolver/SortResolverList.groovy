package com.goshop.catalog.rest.resolver

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.data.domain.Sort
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class SortResolverList implements com.goshop.catalog.rest.resolver.SortResolver {

    private List<com.goshop.catalog.rest.resolver.SortResolver> resolvers

    @Required
    void setResolvers(List<com.goshop.catalog.rest.resolver.SortResolver> resolvers) {
        this.resolvers = resolvers
    }

    @Override
    boolean canResolve(Class<?> entityClass) {
        return resolvers.any { com.goshop.catalog.rest.resolver.SortResolver resolver -> resolver.canResolve(entityClass) }
    }

    @Override
    Sort resolve(Class<?> entityClass, Map<String, Object> options) {
        def resolver = resolvers.find { com.goshop.catalog.rest.resolver.SortResolver resolver -> resolver.canResolve(entityClass) }
        assert resolver != null, "resolver not found"

        return resolver.resolve(entityClass, options)
    }
}
