/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.json;

import java.util.HashSet;
import java.util.Set;

/**
 * PropertyAssignedAwareSupport class.
 */
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
}
