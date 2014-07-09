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
class CreateFilter implements PropertyMappingFilter {

    @Override
    boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return false
    }

    @Override
    void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        boolean writable = context.isPropertyWritable(event.sourcePropertyName)
        if (!writable) {
            if (PropertyAssignedAwareSupport.isPropertyAssigned(event.source, event.sourcePropertyName)) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable(event.sourcePropertyName).exception()
            }
        }
    }

    @Override
    void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
    }
}
