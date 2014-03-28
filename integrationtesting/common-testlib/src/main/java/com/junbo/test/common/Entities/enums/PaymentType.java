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
    CREDITCARD("CREDITCARD");

    private String name;

    private PaymentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
