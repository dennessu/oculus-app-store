/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.property;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Jason
 *         time 3/26/2014
 *         update by Jason at 8/5/2014 to support running a specific group tests
 *         process property annotations:
 *         1. if test status is DefaultEnable or Enable, set enabled=true
 *            and testNG will run this test; else if test status is Manual, Disabled, Incomplete, set
 *            enabled=false and testNG will ignore this test.
 *         2. support running a specific group tests
 *
 */
public class PropertyTransformer implements IAnnotationTransformer {

    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        try {
            Property caseProperty = testMethod.getAnnotation(Property.class);
            if (caseProperty.status().equals(Status.DefaultEnable) ||
                    caseProperty.status().equals(Status.Enable)) {
                annotation.setEnabled(true);
            } else {
                annotation.setEnabled(false);
            }

            if (caseProperty.priority().equals(Priority.BVT)) {
                String[] groups = new String[3];
                groups[0] = Priority.BVT.name();
                groups[1] = Priority.BVT.name().toLowerCase();
                groups[2] = Priority.BVT.name().toUpperCase();
                annotation.setGroups(groups);
            } else if (caseProperty.priority().equals(Priority.Dailies)) {
                String[] groups = new String[3];
                groups[0] = Priority.Dailies.name();
                groups[1] = Priority.Dailies.name().toLowerCase();
                groups[2] = Priority.Dailies.name().toUpperCase();
                annotation.setGroups(groups);
            } else if (caseProperty.priority().equals(Priority.Comprehensive)) {
                String[] groups = new String[3];
                groups[0] = Priority.Comprehensive.name();
                groups[1] = Priority.Comprehensive.name().toLowerCase();
                groups[2] = Priority.Comprehensive.name().toUpperCase();
                annotation.setGroups(groups);
            } else {
                String[] groups = new String[1];
                groups[0] = Priority.Default.name();
                annotation.setGroups(groups);
            }

        } catch (NullPointerException e) {
            return;
        }
    }
}
