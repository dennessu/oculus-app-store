/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity.type;

import com.junbo.order.spec.model.enums.OrderItemRevisionType;

/**
 * Created by chriszhu on 6/12/14.
 */
public class OrderItemRevisionEnumType extends IdentifiableEnumType {
    @Override
    public Class returnedClass() {
        return OrderItemRevisionType.class;
    }
}
