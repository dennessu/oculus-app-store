/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.type;

import com.junbo.cart.common.util.IdentifiableEnumType;
import com.junbo.cart.db.entity.ItemStatus;

/**
 * Created by fzhang@wan-san.com on 14-1-24.
 */
public class ItemStatusType extends IdentifiableEnumType {
    @Override
    public Class returnedClass() {
        return ItemStatus.class;
    }
}
