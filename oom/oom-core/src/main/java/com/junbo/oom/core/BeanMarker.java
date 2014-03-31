/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shenhua on 3/23/2014.
 */
public class BeanMarker {

    public static final TrueBeanMarker TRUE = new TrueBeanMarker();

    public static final FalseBeanMarker FALSE = new FalseBeanMarker();

    private boolean hasItSelfMarked;

    private Map<String, BeanMarker> subBeanMarkers;

    public void markItSelf() {
        hasItSelfMarked = true;
    }

    public void markProperty(String fullPropertyName) {
        if (fullPropertyName == null) {
            throw new IllegalArgumentException("fullPropertyName is null");
        }

        String firstPart;
        String secondPart;

        int pos = fullPropertyName.indexOf('.');
        if (pos >= 0) {
            firstPart = fullPropertyName.substring(0, pos);
            secondPart = fullPropertyName.substring(pos + 1);
        } else {
            firstPart = fullPropertyName;
            secondPart = null;
        }

        if (subBeanMarkers == null) {
            subBeanMarkers = new HashMap<>();
        }

        BeanMarker subBeanMarker = subBeanMarkers.get(firstPart);
        if (subBeanMarker == null) {
            subBeanMarker = new BeanMarker();
            subBeanMarkers.put(firstPart, subBeanMarker);
        }

        if (secondPart == null) {
            subBeanMarker.markItSelf();
        } else {
            subBeanMarker.markProperty(secondPart);
        }
    }

    public void markProperties(String... fullPropertyNames) {
        for (String fullPropertyName : fullPropertyNames) {
            markProperty(fullPropertyName);
        }
    }

    public void markProperties(Iterable<String> fullPropertyNames) {
        for (String fullPropertyName : fullPropertyNames) {
            markProperty(fullPropertyName);
        }
    }

    public boolean hasItSelfMarked() {
        return hasItSelfMarked;
    }

    public boolean hasItSelfPartiallyMarked() {
        return !hasItSelfMarked && subBeanMarkers != null;
    }

    public boolean hasPropertyMarked(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (hasItSelfMarked) {
            return true;
        }

        if (subBeanMarkers == null) {
            return false;
        }

        return subBeanMarkers.containsKey(propertyName)
                && subBeanMarkers.get(propertyName).hasItSelfMarked();
    }

    public boolean hasPropertyPartiallyMarked(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (hasItSelfMarked) {
            return false;
        }

        if (subBeanMarkers == null) {
            return false;
        }

        return subBeanMarkers.containsKey(propertyName)
                && !subBeanMarkers.get(propertyName).hasItSelfMarked();
    }

    public BeanMarker getSubBeanMarker(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (hasItSelfMarked) {
            return TRUE;
        }

        if (subBeanMarkers == null) {
            return FALSE;
        }

        return subBeanMarkers.get(propertyName);
    }

    /**
     * Always True Bean Marker.
     */
    public static class TrueBeanMarker extends BeanMarker {
        @Override
        public void markItSelf() {
            throw new IllegalStateException();
        }

        @Override
        public void markProperty(String fullPropertyName) {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasItSelfMarked() {
            return true;
        }

        @Override
        public boolean hasItSelfPartiallyMarked() {
            return false;
        }

        @Override
        public boolean hasPropertyMarked(String propertyName) {
            return true;
        }

        @Override
        public boolean hasPropertyPartiallyMarked(String propertyName) {
            return false;
        }

        @Override
        public BeanMarker getSubBeanMarker(String propertyName) {
            return this;
        }
    }

    /**
     * Always False Bean Marker.
     */
    public static class FalseBeanMarker extends BeanMarker {
        @Override
        public void markItSelf() {
            throw new IllegalStateException();
        }

        @Override
        public void markProperty(String fullPropertyName) {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasItSelfMarked() {
            return false;
        }

        @Override
        public boolean hasItSelfPartiallyMarked() {
            return false;
        }

        @Override
        public boolean hasPropertyMarked(String propertyName) {
            return false;
        }

        @Override
        public boolean hasPropertyPartiallyMarked(String propertyName) {
            return false;
        }

        @Override
        public BeanMarker getSubBeanMarker(String propertyName) {
            return this;
        }
    }
}
