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

    private Object source;

    private Object alternativeSource;

    private Object sourceProperty;

    private Object alternativeSourceProperty;

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

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getAlternativeSource() {
        return alternativeSource;
    }

    public void setAlternativeSource(Object alternativeSource) {
        this.alternativeSource = alternativeSource;
    }

    public Object getSourceProperty() {
        return sourceProperty;
    }

    public void setSourceProperty(Object sourceProperty) {
        this.sourceProperty = sourceProperty;
    }

    public Object getAlternativeSourceProperty() {
        return alternativeSourceProperty;
    }

    public void setAlternativeSourceProperty(Object alternativeSourceProperty) {
        this.alternativeSourceProperty = alternativeSourceProperty;
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
