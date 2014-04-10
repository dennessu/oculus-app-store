/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity.type;

import com.junbo.order.db.entity.enums.SubledgerItemAction;

/**
 * Created by fzhang on 4/2/2014.
 */
public class SubledgerItemActionType extends IdentifiableEnumType {
    @Override
    public Class returnedClass() {
        return SubledgerItemAction.class;
    }
}
