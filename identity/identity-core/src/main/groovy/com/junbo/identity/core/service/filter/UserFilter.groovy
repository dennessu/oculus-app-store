package com.junbo.identity.core.service.filter

import com.junbo.common.json.PropertyAssignedAwareSupport
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.oom.core.BeanMarker
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.*
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component

/**
 * Created by kg on 3/19/2014.
 */
@Component
@CompileStatic
class UserFilter implements ResourceFilter<User> {

    @Autowired
    private SelfMapper selfMapper

    private List<String> readableProperties

    private List<String> writablePropertiesForCreate

    private List<String> writablePropertiesForUpdate

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
    User filterForCreate(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForCreate)

        def createFilter = new CreateFilter();
        def writablePropertiesFilter = new WritablePropertiesFilter();

        def filterList = new PropertyMappingFilterList(filters: [
                createFilter,
                writablePropertiesFilter,
        ])

        context.propertyMappingFilter = filterList

        return selfMapper.filterUser(user, context)
    }

    @Override
    User filterForPut(User user, User oldUser) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(readableProperties)

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForUpdate)

        def putFilter = new PutFilter();
        def readablePropertiesFilter = new ReadablePropertiesFilter();
        def writablePropertiesFilter = new WritablePropertiesFilter();

        def filterList = new PropertyMappingFilterList(filters: [
                putFilter,
                readablePropertiesFilter,
                writablePropertiesFilter
        ])

        context.propertyMappingFilter = filterList

        return selfMapper.mergeUser(user, oldUser, context)
    }

    @Override
    User filterForPatch(User user, User oldUser) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(readableProperties)

        context.writableProperties = new BeanMarker()
        context.writableProperties.markProperties(writablePropertiesForUpdate)

        def patchFilter = new PatchFilter();
        def readablePropertiesFilter = new ReadablePropertiesFilter();
        def writablePropertiesFilter = new WritablePropertiesFilter();

        def filterList = new PropertyMappingFilterList(filters: [
                patchFilter,
                readablePropertiesFilter,
                writablePropertiesFilter
        ])

        context.propertyMappingFilter = filterList

        return selfMapper.mergeUser(user, oldUser, context)
    }

    @Override
    User filterForGet(User user, List<String> propertiesToInclude) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        def context = new MappingContext()
        context.readableProperties = new BeanMarker()
        context.readableProperties.markProperties(readableProperties)

        def readFilter = new ReadFilter();
        def readablePropertiesFilter = new ReadablePropertiesFilter();

        def filterList = new PropertyMappingFilterList(filters: [
                readFilter,
                readablePropertiesFilter,
        ])

        context.propertyMappingFilter = filterList

        return selfMapper.filterUser(user, context)
    }

    public static class CreateFilter implements PropertyMappingFilter {

        @Override
        boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
            return false
        }

        @Override
        void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
            boolean writable = context.isPropertyWritable(event.sourcePropertyName)
            if (!writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw AppErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                }
            }
        }

        @Override
        void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }
    }

    public static class ReadFilter implements PropertyMappingFilter {

        @Override
        boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
            return !context.isPropertyReadable(event.sourcePropertyName)
        }

        @Override
        void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }

        @Override
        void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }
    }

    public static class PutFilter implements PropertyMappingFilter {

        @Override
        boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }

        @Override
        void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
            boolean readable = context.isPropertyReadable(event.sourcePropertyName)
            boolean writable = context.isPropertyWritable(event.sourcePropertyName)

            if (readable && !writable) { // readonly
                boolean different = false

                if (isSimpleType(event.sourcePropertyType)) {
                    if (event.sourceProperty != event.alternativeSourceProperty) {
                        different = true
                    }
                } else {
                    boolean sourcePropertyIsNull = event.sourceProperty == null
                    boolean alternativeSourcePropertyIsNull = event.alternativeSourceProperty == null

                    if (sourcePropertyIsNull != alternativeSourcePropertyIsNull) {
                        different = true
                    }
                }

                if (different) {
                    if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                        throw AppErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                    } else {
                        event.sourceProperty = event.alternativeSourceProperty
                    }
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw AppErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                }
            }
        }

        @Override
        void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }
    }


    public static class PatchFilter implements PropertyMappingFilter {

        @Override
        boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }

        @Override
        void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
            boolean readable = context.isPropertyReadable(event.sourcePropertyName)
            boolean writable = context.isPropertyWritable(event.sourcePropertyName)

            if (readable && !writable) { // readonly
                boolean different = false

                if (isSimpleType(event.sourcePropertyType)) {
                    if (event.sourceProperty != event.alternativeSourceProperty) {
                        different = true
                    }
                } else {
                    boolean sourcePropertyIsNull = event.sourceProperty == null
                    boolean alternativeSourcePropertyIsNull = event.alternativeSourceProperty == null

                    if (sourcePropertyIsNull != alternativeSourcePropertyIsNull) {
                        different = true
                    }
                }

                if (different) {
                    if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                        throw AppErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                    } else {
                        event.sourceProperty = event.alternativeSourceProperty
                    }
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw AppErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                }
            }

            if (writable) {
                if (!PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    event.sourceProperty = event.alternativeSourceProperty
                }
            }
        }

        @Override
        void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        }
    }


    public static boolean isSimpleType(Class type) {
        return type == String.class || type == Date.class || Number.class.isAssignableFrom(type)
    }
}
