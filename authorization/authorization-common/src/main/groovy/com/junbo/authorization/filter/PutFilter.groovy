/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

import com.junbo.common.json.PropertyAssignedAwareSupport
import com.junbo.oom.core.MappingContext
import com.junbo.oom.core.filter.PropertyMappingEvent
import com.junbo.oom.core.filter.PropertyMappingFilter
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/26/14.
 */
@CompileStatic
class PutFilter implements PropertyMappingFilter {

    @Override
    boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return false
    }

    @Override
    void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        boolean readable = context.isPropertyReadable(event.sourcePropertyName)
        boolean writable = context.isPropertyWritable(event.sourcePropertyName)

        boolean different = false

        if (FilterUtil.isSimpleType(event.sourcePropertyType)) {
            if (event.sourceProperty != event.alternativeSourceProperty) {
                different = true
            }

            if (readable && !writable) { // readonly
                if (different) {
                    throw FilterErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw FilterErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                } else {
                    event.sourceProperty = event.alternativeSourceProperty
                }
            }

            if (writable) {
                event.alternativeSourceProperty = null // always ignore alternativeSourceProperty
            }
        } else {
            boolean sourcePropertyIsNull = event.sourceProperty == null
            boolean alternativeSourcePropertyIsNull = event.alternativeSourceProperty == null

            if (sourcePropertyIsNull != alternativeSourcePropertyIsNull) {
                different = true
            }

            if (readable && !writable) { // readonly
                if (different) {
                    throw FilterErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw FilterErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                } else {
                    if (!alternativeSourcePropertyIsNull) {
                        event.sourceProperty = event.sourcePropertyType.newInstance()
                    }
                }
            }

            if (writable) {
                event.alternativeSourceProperty = null // always ignore alternativeSourceProperty
            }
        }
    }

    @Override
    void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }
}
