package com.junbo.identity.core.service.filter

import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertyMappingEvent
import com.junbo.oom.core.filter.PropertyMappingFilter
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/26/14.
 */
@CompileStatic
class ReadFilter implements PropertyMappingFilter {

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
