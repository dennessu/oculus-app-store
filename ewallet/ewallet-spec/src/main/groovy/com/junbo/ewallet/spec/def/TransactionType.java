/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.def;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * enum for TransactionType.
 */
public enum TransactionType implements Identifiable<Integer> {
    CREDIT(1),
    DEBIT(2),
    REFUND(3);

    private Integer id;

    TransactionType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        throw new NotSupportedException("enum TransactionType not settable");
    }

}
