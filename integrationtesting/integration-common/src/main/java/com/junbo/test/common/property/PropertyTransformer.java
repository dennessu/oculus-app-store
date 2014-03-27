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
 * time 3/26/2014
 * process property annotations: if test status is DefaultEnable or Enable, set enabled=true
 * and testNG will run this test; else if test status is Manual, Disabled, Incomplete, set
 * enabled=false and testNG will ignore this test.
 */
public class PropertyTransformer implements IAnnotationTransformer {

    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Property caseProperty = testMethod.getAnnotation(Property.class);

        if (caseProperty.status().equals(Status.DefaultEnable) ||
                caseProperty.status().equals(Status.Enable)) {
            annotation.setEnabled(true);
        }
        else {
            annotation.setEnabled(false);
        }
    }
}
