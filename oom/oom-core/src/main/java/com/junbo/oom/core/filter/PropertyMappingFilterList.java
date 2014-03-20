/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.filter;

import com.junbo.oom.core.MappingContext;

import java.util.List;

/**
 * Java doc.
 */
public class PropertyMappingFilterList implements PropertyMappingFilter {

    private List<PropertyMappingFilter> filters;

    public List<PropertyMappingFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<PropertyMappingFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean skipPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        if (filters != null) {
            for (PropertyMappingFilter filter : filters) {
                if (filter.skipPropertyMapping(event, context)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void beginPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        if (filters != null) {
            for (PropertyMappingFilter filter : filters) {
                filter.beginPropertyMapping(event, context);
            }
        }
    }

    @Override
    public void endPropertyMapping(PropertyMappingEvent event, MappingContext context) {
        if (filters != null) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                filters.get(i).endPropertyMapping(event, context);
            }
        }
    }
}
