package com.junbo.sharding.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by minhao on 4/20/14.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface WriteMethod {
}
