/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.enums;

/**
 * Created by Yunlong on 4/9/14.
 */
public enum BalanceTaxStatus {

    TAXED("TAXED"),
    NOT_TAXED("NOT_TAXED"),
    FAILED("FAILED");

    private final String taxStatus;

    BalanceTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    @Override
    public String toString() {
        return taxStatus;
    }

}
