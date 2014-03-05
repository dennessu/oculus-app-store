/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core;

import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import java.util.List;
import java.util.Map;
/**
 * Java doc.
 */
public class MappingContext {

    private Boolean skipMapping;

    private PropertyMappingFilter propertyMappingFilter;

    private ElementMappingFilter elementMappingFilter;

    private List<String> propertiesToInclude;

    private List<String> localesToInclude;

    private List<String> countriesToInclude;

    private List<String> currenciesToInclude;

    private List<String> entitiesToExpand;

    private Map<String, Object> attributes;

    public Boolean getSkipMapping() {
        return skipMapping;
    }

    public void setSkipMapping(Boolean skipMapping) {
        this.skipMapping = skipMapping;
    }

    public PropertyMappingFilter getPropertyMappingFilter() {
        return propertyMappingFilter;
    }

    public void setPropertyMappingFilter(PropertyMappingFilter propertyMappingFilter) {
        this.propertyMappingFilter = propertyMappingFilter;
    }

    public ElementMappingFilter getElementMappingFilter() {
        return elementMappingFilter;
    }

    public void setElementMappingFilter(ElementMappingFilter elementMappingFilter) {
        this.elementMappingFilter = elementMappingFilter;
    }

    public List<String> getPropertiesToInclude() {
        return propertiesToInclude;
    }

    public void setPropertiesToInclude(List<String> propertiesToInclude) {
        this.propertiesToInclude = propertiesToInclude;
    }

    public List<String> getLocalesToInclude() {
        return localesToInclude;
    }

    public void setLocalesToInclude(List<String> localesToInclude) {
        this.localesToInclude = localesToInclude;
    }

    public List<String> getCountriesToInclude() {
        return countriesToInclude;
    }

    public void setCountriesToInclude(List<String> countriesToInclude) {
        this.countriesToInclude = countriesToInclude;
    }

    public List<String> getCurrenciesToInclude() {
        return currenciesToInclude;
    }

    public void setCurrenciesToInclude(List<String> currenciesToInclude) {
        this.currenciesToInclude = currenciesToInclude;
    }

    public List<String> getEntitiesToExpand() {
        return entitiesToExpand;
    }

    public void setEntitiesToExpand(List<String> entitiesToExpand) {
        this.entitiesToExpand = entitiesToExpand;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
