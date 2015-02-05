/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.type;

import com.junbo.order.spec.model.enums.OrderPendingActionType;

/**
 * Created by fzhang on 2015/2/2.
 */
public class OrderPendingActionEnumType extends IdentifiableEnumType {

    @Override
    public Class returnedClass() {
        return OrderPendingActionType.class;
    }

}
