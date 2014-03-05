package com.junbo.fulfilment.common.util;

public enum FulfilmentType {
    DIGITAL(100),
    PHYSICAL(200),
    WALLET(-999);

    private Integer id;

    FulfilmentType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
