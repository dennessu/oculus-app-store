/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
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

    protected Map<String, List<String>> readableProperties

    protected Map<String, List<String>> writablePropertiesForCreate

    protected Map<String, List<String>> writablePropertiesForUpdate

    @Required
    void setReadableProperties(Map<String, List<String>> readableProperties) {
        this.readableProperties = readableProperties
    }

    @Required
    void setWritablePropertiesForCreate(Map<String, List<String>> writablePropertiesForCreate) {
        this.writablePropertiesForCreate = writablePropertiesForCreate
    }

    @Required
    void setWritablePropertiesForUpdate(Map<String, List<String>> writablePropertiesForUpdate) {
        this.writablePropertiesForUpdate = writablePropertiesForUpdate
    }

    private Set<String> calculateReadableProperties() {
        Set<String> result = null

        for (String right : readableProperties.keySet()) {
            if (AuthorizeContext.hasRights(right)) {
                List<String> properties = readableProperties.get(right)

                if (result == null) {
                    result = []
                }

                result.addAll(properties)
            }
        }

        return result
    }

    private Set<String> calculateWritablePropertiesForCreate() {
        Set<String> result = null

        for (String right : writablePropertiesForCreate.keySet()) {
            if (AuthorizeContext.hasRights(right)) {
                List<String> properties = writablePropertiesForCreate.get(right)

                if (result == null) {
                    result = []
                }

                result.addAll(properties)
            }
        }

        return result
    }

    private Set<String> calculateWritablePropertiesForUpdate() {
        Set<String> result = null

        for (String right : writablePropertiesForUpdate.keySet()) {
            if (AuthorizeContext.hasRights(right)) {
                List<String> properties = writablePropertiesForUpdate.get(right)

                if (result == null) {
                    result = []
                }

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

        def writableProperties = calculateWritablePropertiesForCreate()
        if (writableProperties != null) {
            context.writableProperties = new BeanMarker()
            context.writableProperties.markProperties(writableProperties)
        } else {
            throw AppCommonErrors.INSTANCE.resourceNotWritable().exception()
        }

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

        def readableProperties = calculateReadableProperties()
        if (readableProperties != null) {
            context.readableProperties = new BeanMarker()
            context.readableProperties.markProperties(readableProperties)
        }

        def writableProperties = calculateWritablePropertiesForUpdate()
        if (writableProperties != null) {
            context.writableProperties = new BeanMarker()
            context.writableProperties.markProperties(writableProperties)
        } else {
            throw AppCommonErrors.INSTANCE.resourceNotWritable().exception()
        }

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

        def readableProperties = calculateReadableProperties()
        if (readableProperties != null) {
            context.readableProperties = new BeanMarker()
            context.readableProperties.markProperties(readableProperties)
        }

        def writableProperties = calculateWritablePropertiesForUpdate()
        if (writableProperties != null) {
            context.writableProperties = new BeanMarker()
            context.writableProperties.markProperties(writableProperties)
        } else {
            throw AppCommonErrors.INSTANCE.resourceNotWritable().exception()
        }

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

        def readableProperties = calculateReadableProperties()
        if (readableProperties != null) {
            context.readableProperties = new BeanMarker()
            context.readableProperties.markProperties(calculateReadableProperties())
        } else {
            return null
        }

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
