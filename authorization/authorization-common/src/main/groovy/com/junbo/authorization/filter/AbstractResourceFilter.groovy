/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.junbo.authorization.AuthorizeContext
import com.junbo.oom.core.BeanMarker
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertiesToIncludeFilter
import com.junbo.oom.core.filter.PropertyMappingFilterList
import com.junbo.oom.core.filter.ReadablePropertiesFilter
import com.junbo.oom.core.filter.WritablePropertiesFilter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/19/2014.
 */
@CompileStatic
abstract class AbstractResourceFilter<T> implements ResourceFilter<T> {

    protected Multimap<String, String> readableProperties

    protected Multimap<String, String> writablePropertiesForCreate

    protected Multimap<String, String> writablePropertiesForUpdate

    @Required
    void setReadableProperties(Map<String, String> readableProperties) {
        this.readableProperties = HashMultimap.create()

        for (String right : readableProperties.keySet()) {
            String value = readableProperties.get(right)

            for (String property : value.split(',')) {
                property = property.trim()
                if (!property.empty) {
                    this.readableProperties.put(right, property)
                }
            }
        }
    }

    @Required
    void setWritablePropertiesForCreate(Map<String, String> writablePropertiesForCreate) {
        this.writablePropertiesForCreate = HashMultimap.create()

        for (String right : writablePropertiesForCreate.keySet()) {
            String value = writablePropertiesForCreate.get(right)

            for (String property : value.split(',')) {
                property = property.trim()
                if (!property.empty) {
                    this.writablePropertiesForCreate.put(right, property)
                }
            }
        }
    }

    @Required
    void setWritablePropertiesForUpdate(Map<String, String> writablePropertiesForUpdate) {
        this.writablePropertiesForUpdate = HashMultimap.create()

        for (String right : writablePropertiesForUpdate.keySet()) {
            String value = writablePropertiesForUpdate.get(right)

            for (String property : value.split(',')) {
                property = property.trim()
                if (!property.empty) {
                    this.writablePropertiesForUpdate.put(right, property)
                }
            }
        }
    }

    private Set<String> calculateReadableProperties() {
        Set<String> result = []

        for (String scope : readableProperties.keySet()) {
            if (AuthorizeContext.hasScopes(scope)) {
                def properties = readableProperties.get(scope)
                result.addAll(properties)

            }
        }

        return result
    }

    private Set<String> calculateWritablePropertiesForCreate() {
        Set<String> result = []

        for (String scope : writablePropertiesForCreate.keySet()) {
            if (AuthorizeContext.hasScopes(scope)) {
                def properties = writablePropertiesForCreate.get(scope)
                result.addAll(properties)

            }
        }

        return result
    }

    private Set<String> calculateWritablePropertiesForUpdate() {
        Set<String> result = []

        for (String scope : writablePropertiesForUpdate.keySet()) {
            if (AuthorizeContext.hasScopes(scope)) {
                def properties = writablePropertiesForUpdate.get(scope)
                result.addAll(properties)

            }
        }

        return result
    }

    @Override
    T filterForCreate(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(calculateWritablePropertiesForCreate())

        def createFilter = new CreateFilter()
        def writablePropertiesFilter = new WritablePropertiesFilter()

        def filterList = new PropertyMappingFilterList(filters: [
                createFilter,
                writablePropertiesFilter,
        ])

        context.propertyMappingFilter = filterList

        return filter(obj, context)
    }

    @Override
    T filterForPut(T obj, T oldObj) {
        if (obj == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(calculateReadableProperties())

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(calculateWritablePropertiesForUpdate())

        def putFilter = new PutFilter()
        def readablePropertiesFilter = new ReadablePropertiesFilter()
        def writablePropertiesFilter = new WritablePropertiesFilter()

        def filterList = new PropertyMappingFilterList(filters: [
                putFilter,
                readablePropertiesFilter,
                writablePropertiesFilter
        ])

        context.propertyMappingFilter = filterList

        return merge(obj, oldObj, context)
    }

    @Override
    T filterForPatch(T obj, T oldObj) {
        if (obj == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(calculateReadableProperties())

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(calculateWritablePropertiesForUpdate())

        def patchFilter = new PatchFilter()
        def readablePropertiesFilter = new ReadablePropertiesFilter()
        def writablePropertiesFilter = new WritablePropertiesFilter()

        def filterList = new PropertyMappingFilterList(filters: [
                patchFilter,
                readablePropertiesFilter,
                writablePropertiesFilter
        ])

        context.propertyMappingFilter = filterList

        return merge(obj, oldObj, context)
    }

    @Override
    T filterForGet(T obj, List<String> propertiesToInclude) {
        if (obj == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(calculateReadableProperties())

        def readFilter = new ReadFilter()
        def readablePropertiesFilter = new ReadablePropertiesFilter()

        def filterList = new PropertyMappingFilterList(filters: [
                readFilter,
                readablePropertiesFilter,
        ])

        if (propertiesToInclude != null) {
            context.propertiesToInclude = new BeanMarker()
            context.propertiesToInclude.markProperties(propertiesToInclude)

            filterList.filters.add(new PropertiesToIncludeFilter())
        }

        context.propertyMappingFilter = filterList

        return filter(obj, context)
    }

    protected abstract T filter(T model, MappingContext context)

    protected abstract T merge(T source, T base, MappingContext context)
}
