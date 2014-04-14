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
public class ItemMappingEvent {

    private Class<?> sourceType;

    private Class<?> sourceItemType;

    private int sourceItemIndex;

    private Object source;

    private Object alternativeSource;

    private Object sourceItem;

    private Object alternativeSourceItem;

    private Class<?> targetType;

    private Class<?> targetItemType;

    private int targetItemIndex;

    private Map<String, Object> attributes;

    public Class<?> getSourceItemType() {
        return sourceItemType;
    }

    public void setSourceItemType(Class<?> sourceItemType) {
        this.sourceItemType = sourceItemType;
    }

    public int getSourceItemIndex() {
        return sourceItemIndex;
    }

    public void setSourceItemIndex(int sourceItemIndex) {
        this.sourceItemIndex = sourceItemIndex;
    }

    public Object getSourceItem() {
        return sourceItem;
    }

    public Object getAlternativeSourceItem() {
        return alternativeSourceItem;
    }

    public void setAlternativeSourceItem(Object alternativeSourceItem) {
        this.alternativeSourceItem = alternativeSourceItem;
    }

    public void setSourceItem(Object sourceItem) {
        this.sourceItem = sourceItem;
    }

    public Class<?> getTargetItemType() {
        return targetItemType;
    }

    public void setTargetItemType(Class<?> targetItemType) {
        this.targetItemType = targetItemType;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public void setSourceType(Class<?> sourceType) {
        this.sourceType = sourceType;
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

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }

    public int getTargetItemIndex() {
        return targetItemIndex;
    }

    public void setTargetItemIndex(int targetItemIndex) {
        this.targetItemIndex = targetItemIndex;
    }
}
