/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.property;

import java.lang.annotation.Retention;

/**
 * @author Jason
 * time 3/11/2014
 * test properties: priority, features, component, owner, status, description, steps
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Property {
    Priority priority() default Priority.Default;
    String features() default "";
    Component[] component() default Component.Default;
    String owner() default "";
    Status status() default Status.DefaultEnable;
    String description() default "";
    String[] steps() default {};
    String bugNum() default "";
    String environment() default "";
    Release release() default Release.Default;
}
