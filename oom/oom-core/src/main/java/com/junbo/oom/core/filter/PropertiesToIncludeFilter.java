/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.filter;

import com.junbo.oom.core.MappingContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Java doc.
 */
public class PropertiesToIncludeFilter implements PropertyMappingFilter {

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        List<String> toInclude = context.getPropertiesToInclude();
        if (toInclude != null && toInclude.size() != 0) {

            boolean found = false;
            for (String property : toInclude) {
                if (property.equals(event.getTargetPropertyName()) ||
                        property.startsWith(event.getTargetPropertyName() + ".")) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        List<String> toInclude = context.getPropertiesToInclude();
        if (toInclude != null && toInclude.size() != 0) {
            List<String> filtered = null;

            for (String property : toInclude) {
                if (property.equals(event.getTargetPropertyName())) {
                    filtered = null;
                    break;
                }

                if (property.startsWith(event.getTargetPropertyName() + ".")) {
                    if (filtered == null) {
                        filtered = new ArrayList<>();
                    }

                    filtered.add(property.substring(event.getTargetPropertyName().length() + 1));
                }
            }

            if (event.getAttributes() == null) {
                event.setAttributes(new HashMap<String, Object>());
            }

            event.getAttributes().put("propertiesToIncludeOld", toInclude);
            context.setPropertiesToInclude(filtered);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        if (event.getAttributes() != null && event.getAttributes().get("propertiesToIncludeOld") != null) {
            context.setPropertiesToInclude((List<String>) event.getAttributes().get("propertiesToIncludeOld"));
        }
    }
}
