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
public class ItemMappingFilterList implements ItemMappingFilter {

    private List<ItemMappingFilter> filters;

    public List<ItemMappingFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<ItemMappingFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean skipItemMapping(ItemMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (ItemMappingFilter filter : filters) {
                if (filter.skipItemMapping(event, context)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void beginItemMapping(ItemMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (ItemMappingFilter filter : filters) {
                filter.beginItemMapping(event, context);
            }
        }
    }

    @Override
    public void endItemMapping(ItemMappingEvent event, MappingContext context) {

        if (filters != null) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                filters.get(i).endItemMapping(event, context);
            }
        }
    }
}
