/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Created by Yunlong on 3/25/14.
 */
public enum PaymentType {
    CREDITCARD("CREDITCARD", 0L),
    DIRECTDEBIT("DIRECTDEBIT", 0L),
    PAYPAL("PAYPAL", 3L),
    EWALLET("WALLET", 2L),
    OTHERS("OTHERS", 4L),
    FAKE("FAKE", 5L);

    private String name;
    private Long type;

    private PaymentType(String name, Long type) {
        this.name = name;
        this.type = type;
    }

    public Long getValue() {
        return this.type;
    }

    @Override
    public String toString() {
        return name;
    }

}
