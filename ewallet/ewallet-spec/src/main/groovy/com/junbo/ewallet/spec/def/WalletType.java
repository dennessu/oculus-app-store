/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.def;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Enum for WalletType.
 */
public enum WalletType implements Identifiable<Integer> {
    STORED_VALUE(1),
    VIRTUAL_CURRENCY(2);

    private Integer id;

    WalletType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        throw new NotSupportedException("enum WalletType not settable");
    }

}
