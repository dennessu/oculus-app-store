/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.def;

import com.junbo.common.util.Identifiable;

/**
 * enum for TransactionType.
 */
public enum TransactionType implements Identifiable<Integer> {
    CREDIT(1),
    DEBIT(2);

    private Integer id;

    TransactionType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
