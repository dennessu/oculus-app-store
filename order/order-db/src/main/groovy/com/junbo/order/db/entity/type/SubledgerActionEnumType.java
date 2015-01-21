/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.type;

import com.junbo.order.spec.model.enums.SubledgerActionType;

/**
 * Created by fzhang on 2015/1/18.
 */
public class SubledgerActionEnumType extends IdentifiableEnumType {

    @Override
    public Class returnedClass() {
        return SubledgerActionType.class;
    }

}
