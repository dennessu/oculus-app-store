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
public class ElementMappingEvent {

    private Class<?> sourceElementType;

    private Object sourceElement;

    private Class<?> targetElementType;

    private Map<String, Object> attributes;

    public Class<?> getSourceElementType() {
        return sourceElementType;
    }

    public void setSourceElementType(Class<?> sourceElementType) {
        this.sourceElementType = sourceElementType;
    }

    public Object getSourceElement() {
        return sourceElement;
    }

    public void setSourceElement(Object sourceElement) {
        this.sourceElement = sourceElement;
    }

    public Class<?> getTargetElementType() {
        return targetElementType;
    }

    public void setTargetElementType(Class<?> targetElementType) {
        this.targetElementType = targetElementType;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
