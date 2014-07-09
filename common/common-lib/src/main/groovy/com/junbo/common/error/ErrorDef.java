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
    int httpStatusCode();

    String message() default "";

    String code();

    String field() default "";

    String reason() default "";
}
