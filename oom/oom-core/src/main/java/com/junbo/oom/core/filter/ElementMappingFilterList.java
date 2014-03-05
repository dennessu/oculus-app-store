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
public class ElementMappingFilterList implements ElementMappingFilter {

    private List<ElementMappingFilter> filters;

    public List<ElementMappingFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ElementMappingFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean skipElementMapping(ElementMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (ElementMappingFilter filter : filters) {
                if (filter.skipElementMapping(event, context)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void beginElementMapping(ElementMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (ElementMappingFilter filter : filters) {
                filter.beginElementMapping(event, context);
            }
        }
    }

    @Override
    public void endElementMapping(ElementMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                filters.get(i).endElementMapping(event, context);
            }
        }
    }
}
