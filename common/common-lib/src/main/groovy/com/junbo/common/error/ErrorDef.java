/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Interface for AppError.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorDef {
    int httpStatusCode() default 500;

    String code() default "";

    String description() default "";

    String field() default "";
}
