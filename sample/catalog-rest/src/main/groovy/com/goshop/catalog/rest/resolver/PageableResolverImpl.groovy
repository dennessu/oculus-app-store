package com.goshop.catalog.rest.resolver

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class PageableResolverImpl implements com.goshop.catalog.rest.resolver.PageableResolver {

    private int defaultPageSize

    private int maxPageSize

    private SortResolver sortResolver

    @Required
    void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize
    }

    @Required
    void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize
    }

    @Required
    void setSortResolver(SortResolver sortResolver) {
        this.sortResolver = sortResolver
    }

    @Override
    boolean canResolve(Class<?> entityClass) {
        return true && sortResolver.canResolve(entityClass)
    }

    @Override
    Pageable resolve(Class<?> entityClass, Map<String, Object> options) {
        int page = 0

        if (options["page"] != null) {
            page = (int) options["page"]
        }

        if (page < 0) {
            page = 0
        }

        int size = defaultPageSize

        if (options["size"] != null) {
            size = (int) options["size"]
        }

        if (size < 1) {
            size = 1
        }

        if (size > maxPageSize) {
            size = maxPageSize
        }

        def sort = sortResolver.resolve(entityClass, options)

        return new PageRequest(page, size, sort)
    }
}
