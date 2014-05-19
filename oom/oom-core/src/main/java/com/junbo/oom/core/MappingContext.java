/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core;

import com.junbo.oom.core.filter.ItemMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import java.util.List;
import java.util.Map;

/**
 * Java doc.
 */
public class MappingContext {

    private Boolean skipMapping;

    private Boolean setsNull;

    private Boolean addsNull;

    private PropertyMappingFilter propertyMappingFilter;

    private ItemMappingFilter itemMappingFilter;

    private BeanMarker propertiesToInclude;

    private List<String> entitiesToExpand;

    private BeanMarker readableProperties;

    private BeanMarker writableProperties;

    private Map<String, Object> attributes;

    public Boolean getSkipMapping() {
        return skipMapping;
    }

    public void setSkipMapping(Boolean skipMapping) {
        this.skipMapping = skipMapping;
    }

    public Boolean getSetsNull() {
        return setsNull;
    }

    public void setSetsNull(Boolean setsNull) {
        this.setsNull = setsNull;
    }

    public Boolean getAddsNull() {
        return addsNull;
    }

    public void setAddsNull(Boolean addsNull) {
        this.addsNull = addsNull;
    }

    public PropertyMappingFilter getPropertyMappingFilter() {
        return propertyMappingFilter;
    }

    public void setPropertyMappingFilter(PropertyMappingFilter propertyMappingFilter) {
        this.propertyMappingFilter = propertyMappingFilter;
    }

    public ItemMappingFilter getItemMappingFilter() {
        return itemMappingFilter;
    }

    public void setItemMappingFilter(ItemMappingFilter itemMappingFilter) {
        this.itemMappingFilter = itemMappingFilter;
    }

    public BeanMarker getPropertiesToInclude() {
        return propertiesToInclude;
    }

    public void setPropertiesToInclude(BeanMarker propertiesToInclude) {
        this.propertiesToInclude = propertiesToInclude;
    }

    public List<String> getEntitiesToExpand() {
        return entitiesToExpand;
    }

    public void setEntitiesToExpand(List<String> entitiesToExpand) {
        this.entitiesToExpand = entitiesToExpand;
    }

    public BeanMarker getReadableProperties() {
        return readableProperties;
    }

    public void setReadableProperties(BeanMarker readableProperties) {
        this.readableProperties = readableProperties;
    }

    public BeanMarker getWritableProperties() {
        return writableProperties;
    }

    public void setWritableProperties(BeanMarker writableProperties) {
        this.writableProperties = writableProperties;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean isPropertyReadable(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (readableProperties == null) {
            return false;
        }

        return readableProperties.hasPropertyMarked(propertyName)
                || readableProperties.hasPropertyPartiallyMarked(propertyName);
    }

    public boolean isPropertyWritable(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (writableProperties == null) {
            return false;
        }

        return writableProperties.hasPropertyMarked(propertyName);
    }


    public boolean isItemReadable() {
        if (readableProperties == null) {
            return false;
        }

        return readableProperties.hasItSelfMarked()
                || readableProperties.hasItSelfPartiallyMarked();
    }

    public boolean isItemWritable() {
        if (writableProperties == null) {
            return false;
        }

        return writableProperties.hasItSelfMarked();
    }
}
