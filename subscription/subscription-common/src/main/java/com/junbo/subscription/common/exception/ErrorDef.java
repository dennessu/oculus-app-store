package com.junbo.subscription.common.exception;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorDef {
    int httpStatusCode() default 500;

    String code() default "";

    String message() default "";

    String field() default "";
}
