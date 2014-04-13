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
public class WritablePropertiesFilter implements PropertyMappingFilter {

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return false;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        BeanMarker filtered = null;

        BeanMarker writableProperties = context.getWritableProperties();
        if (writableProperties != null) {
            filtered = writableProperties.getSubBeanMarker(event.getSourcePropertyName());
        }

        if (filtered != writableProperties) {
            event.getSubAttributes(event).put("writablePropertiesOld", writableProperties);
            context.setWritableProperties(filtered);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        Map<Object, Object> subAttributes = event.getSubAttributes(event);

        if (subAttributes.containsKey("writablePropertiesOld")) {
            context.setWritableProperties((BeanMarker) subAttributes.get("writablePropertiesOld"));
            subAttributes.remove("writablePropertiesOld");
        }
    }
}
