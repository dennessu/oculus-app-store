/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.def;

import com.junbo.common.util.Identifiable;

/**
 * Enum for WalletLotType. Can be sorted by type.
 */
public enum WalletLotType implements Identifiable<Integer> {
    CASH(0),
    PROMOTION(1000);

    private Integer id;

    WalletLotType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
