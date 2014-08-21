/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java doc for XSSFreeRichText.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
//@JsonDeserialize(using = XSSFreeRichTextDeserializer.class)
public @interface XSSFreeRichText {
}
