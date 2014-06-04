/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.junbo.common.jackson.deserializer.ResourceIdDeserializer;
import com.junbo.common.jackson.serializer.ResourceIdSerializer;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java doc for ShippingAddressId.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = ResourceIdSerializer.class)
@JsonDeserialize(using = ResourceIdDeserializer.class)
@ResourcePath("/personal-info")
public @interface ShippingToPhoneId {
}
