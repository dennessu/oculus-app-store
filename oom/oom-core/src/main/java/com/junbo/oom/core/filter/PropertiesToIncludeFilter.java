/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.filter;

import com.junbo.oom.core.BeanMarker;
import com.junbo.oom.core.MappingContext;

import java.util.Map;

/**
 * Java doc.
 */
public class PropertiesToIncludeFilter implements PropertyMappingFilter {

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        BeanMarker toInclude = context.getPropertiesToInclude();
        if (toInclude != null) {
            return !toInclude.hasPropertyMarked(event.getTargetPropertyName())
                    && !toInclude.hasPropertyPartiallyMarked(event.getTargetPropertyName());
        }

        return false;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        BeanMarker filtered = null;

        BeanMarker toInclude = context.getPropertiesToInclude();
        if (toInclude != null) {
            filtered = toInclude.getSubBeanMarker(event.getTargetPropertyName());
        }

        if (filtered != toInclude) {
            event.getSubAttributes(event).put("propertiesToIncludeOld", toInclude);
            context.setPropertiesToInclude(filtered);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        Map<Object, Object> subAttributes = event.getSubAttributes(event);

        if (subAttributes.containsKey("propertiesToIncludeOld")) {
            context.setPropertiesToInclude((BeanMarker) subAttributes.get("propertiesToIncludeOld"));
            subAttributes.remove("propertiesToIncludeOld");
        }
    }
}
