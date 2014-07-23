package com.junbo.identity.core.service.filter

import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.RightsScope
import com.junbo.common.model.AdminInfo
import com.junbo.common.model.ResourceMeta
import com.junbo.common.model.ResourceMetaBase
import com.junbo.oom.core.BeanMarker
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertiesToIncludeFilter
import com.junbo.oom.core.filter.PropertyMappingFilterList
import com.junbo.oom.core.filter.ReadablePropertiesFilter
import com.junbo.oom.core.filter.WritablePropertiesFilter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/19/2014.
 */
@CompileStatic
abstract class ResourceFilterImpl<T extends ResourceMeta> implements ResourceFilter<T> {

    private static final String IDENTITY_ADMIN_SCOPE = 'identity.admin'

    @Autowired
    protected SelfMapper selfMapper

    protected List<String> readableProperties

    protected List<String> writablePropertiesForCreate

    protected List<String> writablePropertiesForUpdate

    @Required
    void setReadableProperties(List<String> readableProperties) {
        this.readableProperties = readableProperties
    }

    @Required
    void setWritablePropertiesForCreate(List<String> writablePropertiesForCreate) {
        this.writablePropertiesForCreate = writablePropertiesForCreate
    }

    @Required
    void setWritablePropertiesForUpdate(List<String> writablePropertiesForUpdate) {
        this.writablePropertiesForUpdate = writablePropertiesForUpdate
    }

    @Override
    T filterForCreate(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForCreate)

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
        context.readableProperties.markProperties(readableProperties)

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForUpdate)

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
        context.readableProperties.markProperties(readableProperties)

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForUpdate)

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
        context.readableProperties.markProperties(readableProperties)

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

        T newObj = filter(obj, context)
        if (ResourceMetaBase.isAssignableFrom(newObj.class)) {
            filterForAdminInfo(newObj as ResourceMetaBase)
        }
        return newObj
    }

    protected abstract T filter(T model, MappingContext context)

    protected abstract T merge(T source, T base, MappingContext context)

    static <T extends ResourceMetaBase> T filterForAdminInfo(T resourceMeta) {
        if (resourceMeta == null) {
            return resourceMeta
        }

        if (AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
           AdminInfo adminInfo = new AdminInfo(
                    createdByClient: resourceMeta.createdByClient,
                    createdBy: resourceMeta.createdBy,
                    updatedByClient: resourceMeta.updatedByClient,
                    updatedBy: resourceMeta.updatedBy
            )
            resourceMeta.setAdminInfo(adminInfo)
        } else {
            resourceMeta.setAdminInfo(null)
        }
        return resourceMeta
    }
}
