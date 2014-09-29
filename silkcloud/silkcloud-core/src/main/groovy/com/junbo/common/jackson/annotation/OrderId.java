/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.junbo.common.jackson.deserializer.OrderIdDeserializer;
import com.junbo.common.jackson.serializer.OrderIdSerializer;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java doc for OrderId.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = OrderIdSerializer.class)
@JsonDeserialize(using = OrderIdDeserializer.class)
@ResourcePath("/orders")
public @interface OrderId {
}
