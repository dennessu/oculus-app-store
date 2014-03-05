/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core.filter;

import java.util.Map;
/**
 * Java doc.
 */
public class PropertyMappingEvent {

    private Class<?> sourceType;

    private Class<?> sourcePropertyType;

    private String sourcePropertyName;

    private Object sourceProperty;

    private Class<?> targetType;

    private Class<?> targetPropertyType;

    private String targetPropertyName;

    private Map<String, Object> attributes;

    public Class<?> getSourceType() {
        return sourceType;
    }

    public void setSourceType(Class<?> sourceType) {
        this.sourceType = sourceType;
    }

    public Class<?> getSourcePropertyType() {
        return sourcePropertyType;
    }

    public void setSourcePropertyType(Class<?> sourcePropertyType) {
        this.sourcePropertyType = sourcePropertyType;
    }

    public String getSourcePropertyName() {
        return sourcePropertyName;
    }

    public void setSourcePropertyName(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName;
    }

    public Object getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(Object sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }

    public Class<?> getTargetPropertyType() {
        return targetPropertyType;
    }

    public void setTargetPropertyType(Class<?> targetPropertyType) {
        this.targetPropertyType = targetPropertyType;
    }

    public String getTargetPropertyName() {
        return targetPropertyName;
    }

    public void setTargetPropertyName(String targetPropertyName) {
        this.targetPropertyName = targetPropertyName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
