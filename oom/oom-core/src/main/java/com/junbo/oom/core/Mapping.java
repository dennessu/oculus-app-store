/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.core;

import java.lang.annotation.Target;
/**
 * Java doc.
 */
@Target({})
public @interface Mapping {

    String source();

    String target() default "";

    boolean excluded() default false;

    boolean bidirectional() default true;

    String explicitMethod() default "";
}
