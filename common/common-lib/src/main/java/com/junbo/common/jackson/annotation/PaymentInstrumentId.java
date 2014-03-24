/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.junbo.common.jackson.deserializer.CompoundIdDeserializer;
import com.junbo.common.jackson.serializer.CompoundIdSerializer;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * interface for paymentInstrumentId.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = CompoundIdSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
@JsonDeserialize(using = CompoundIdDeserializer.class)
@ResourcePath("/users/{userId}/payment-instruments/{paymentInstrumentId}")
public @interface PaymentInstrumentId {
}
