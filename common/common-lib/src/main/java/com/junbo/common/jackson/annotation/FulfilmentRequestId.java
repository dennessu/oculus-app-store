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
import com.junbo.common.jackson.serializer.CascadeResourceIdSerializer;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java doc.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = CascadeResourceIdSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
@JsonDeserialize(using = ResourceIdDeserializer.class)
@ResourcePath("/fulfilment-requests?orderId=%s")
public @interface FulfilmentRequestId {
}
