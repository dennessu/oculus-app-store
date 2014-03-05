package com.junbo.fulfilment.common.util;

public enum FulfilmentStatus implements Identifiable<Integer> {
    PENDING(100),
    SUCCEED(200),
    FAILED(-999);

    private Integer id;

    FulfilmentStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
