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
public class ReadablePropertiesFilter implements PropertyMappingFilter {

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        return false;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        BeanMarker filtered = null;

        BeanMarker readableProperties = context.getReadableProperties();
        if (readableProperties != null) {
            filtered = readableProperties.getSubBeanMarker(event.getSourcePropertyName());
        }

        if (filtered != readableProperties) {
            event.getSubAttributes(event).put("readablePropertiesOld", readableProperties);
            context.setReadableProperties(filtered);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        Map<Object, Object> subAttributes = event.getSubAttributes(event);

        if (subAttributes.containsKey("readablePropertiesOld")) {
            context.setReadableProperties((BeanMarker) subAttributes.get("readablePropertiesOld"));
            subAttributes.remove("readablePropertiesOld");
        }
    }
}
