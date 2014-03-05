package com.goshop.catalog.rest.resolver

import com.mysema.query.types.Predicate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class PredicateResolverList implements PredicateResolver {

    private List<PredicateResolver> resolvers

    @Required
    void setResolvers(List<PredicateResolver> resolvers) {
        this.resolvers = resolvers
    }

    @Override
    boolean canResolve(Class<?> entityClass) {
        return resolvers.any { PredicateResolver resolver -> resolver.canResolve(entityClass) }
    }

    @Override
    Predicate resolve(Class<?> entityClass, Map<String, Object> options) {
        def resolver = resolvers.find { PredicateResolver resolver -> resolver.canResolve(entityClass) }
        assert resolver != null, "resolver not found"

        return resolver.resolve(entityClass, options)
    }
}
