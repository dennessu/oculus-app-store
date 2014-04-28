/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.*;

/**
 * HATEOAS Link.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonIgnore
@Target(ElementType.FIELD)
public @interface HateoasLink {
    String value();
}
