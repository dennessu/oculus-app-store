/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity.type;

import com.junbo.order.spec.model.enums.ItemType;

/**
 * Created by LinYi on 14-3-7.
 */
public class ItemEnumType extends IdentifiableEnumType {
    @Override
    public Class returnedClass() {
        return ItemType.class;
    }
}
