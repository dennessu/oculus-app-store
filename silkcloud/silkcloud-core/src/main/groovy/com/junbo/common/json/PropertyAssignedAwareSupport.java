/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.json;

import com.junbo.common.cloudant.json.annotations.CloudantIgnore;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * PropertyAssignedAwareSupport class.
 */
@CloudantIgnore
public class PropertyAssignedAwareSupport {

    private final Set<String> assignedProperties;

    public PropertyAssignedAwareSupport() {
        assignedProperties = new HashSet<>();
    }

    public void setPropertyAssigned(String propertyName) {
        assignedProperties.add(propertyName);
    }

    public boolean isPropertyAssigned(String propertyName) {
        return assignedProperties.contains(propertyName);
    }


    public static boolean isPropertyAssigned(Object bean, String propertyName) {
        if (bean == null) {
            throw new IllegalArgumentException("bean is null");
        }

        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }

        if (bean instanceof PropertyAssignedAware) {
            return ((PropertyAssignedAware) bean).isPropertyAssigned(propertyName);
        }

        if (bean instanceof Map) {
            return ((Map) bean).containsKey(propertyName);
        }

        return true;
    }
}
