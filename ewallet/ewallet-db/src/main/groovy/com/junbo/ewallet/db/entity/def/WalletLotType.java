/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.def;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for WalletLotType. Can be sorted by type.
 */
public enum WalletLotType implements PersistedEnum<WalletLotType> {
    CASH(0),
    PROMOTION(1000);

    public static final Map<Integer, WalletLotType> MAP = new HashMap<Integer, WalletLotType>();

    static {
        for (WalletLotType type : WalletLotType.values()) {
            MAP.put(type.getPersistedValue(), type);
        }
    }

    private Integer persistedValue;

    WalletLotType(Integer persistedValue) {
        this.persistedValue = persistedValue;
    }

    public Integer getPersistedValue() {
        return persistedValue;
    }

    public WalletLotType returnEnum(Integer persistedValue) {
        return MAP.get(persistedValue);
    }
}
