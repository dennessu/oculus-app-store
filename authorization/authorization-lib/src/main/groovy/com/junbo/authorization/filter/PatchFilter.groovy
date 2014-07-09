/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.filter

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.json.PropertyAssignedAwareSupport
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

        boolean different = false

        if (FilterUtil.isSimpleType(event.sourcePropertyType)) {
            if (!FilterUtil.equalsSimpleType(event.sourceProperty, event.alternativeSourceProperty)) {
                different = true
            }

            if (readable && !writable) { // readonly
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    if (different) {
                        throw AppCommonErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                    }
                } else {
                    event.sourceProperty = event.alternativeSourceProperty
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw AppCommonErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                } else {
                    event.sourceProperty = event.alternativeSourceProperty
                }
            }

            if (writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    event.alternativeSourceProperty = null
                } else {
                    event.sourceProperty = event.alternativeSourceProperty
                }
            }
        } else {
            boolean sourcePropertyIsNull = event.sourceProperty == null
            boolean alternativeSourcePropertyIsNull = event.alternativeSourceProperty == null

            if (sourcePropertyIsNull != alternativeSourcePropertyIsNull) {
                different = true
            }

            if (readable && !writable) { // readonly
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    if (different) {
                        throw AppCommonErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                    }
                } else {
                    if (!alternativeSourcePropertyIsNull) {
                        event.sourceProperty = initInstance(event.sourcePropertyType)
                    }
                }
            }

            if (!readable && !writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    throw AppCommonErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
                } else {
                    if (!alternativeSourcePropertyIsNull) {
                        event.sourceProperty = initInstance(event.sourcePropertyType)
                    }
                }
            }

            if (writable) {
                if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                    event.alternativeSourceProperty = null
                } else {
                    if (!alternativeSourcePropertyIsNull) {
                        event.sourceProperty = initInstance(event.sourcePropertyType)
                    }
                }
            }
        }
    }

    @Override
    void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }

    private Object initInstance(Class cls) {
        if (cls == Map.class) {
            return new HashMap()
        } else if (cls == List.class) {
            return new ArrayList()
        } else {
            return cls.newInstance()
        }
    }
}
