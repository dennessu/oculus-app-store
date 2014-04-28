package com.junbo.identity.core.service.filter

import com.junbo.common.json.PropertyAssignedAwareSupport
import com.junbo.identity.core.service.util.FilterUtil
import com.junbo.identity.spec.error.AppErrors
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertyMappingEvent
import com.junbo.oom.core.filter.PropertyMappingFilter
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/26/14.
 */
@CompileStatic
class PatchFilter implements PropertyMappingFilter {

    @Override
    boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return false
    }

    @Override
    void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        boolean readable = context.isPropertyReadable(event.sourcePropertyName)
        boolean writable = context.isPropertyWritable(event.sourcePropertyName)

        if (readable && !writable) { // readonly
            boolean different = false

            if (FilterUtil.isSimpleType(event.sourcePropertyType)) {
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
            } else {
                event.alternativeSourceProperty = null // ignore alternativeSourceProperty
            }
        }
    }

    @Override
    void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }
}
