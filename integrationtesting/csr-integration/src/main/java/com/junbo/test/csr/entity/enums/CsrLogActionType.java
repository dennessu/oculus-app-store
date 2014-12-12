/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.entity.enums;

/**
 * Created by weiyu_000 on 11/26/14.
 */

public enum CsrLogActionType {
    TroubleshootResolved(0, "TroubleshootResolved"),
    RefundIssued(1, "RefundIssued"),
    ResetPasswordEmailSent(2, "ResetPasswordEmailSent"),
    VerificationEmailSent(3, "VerificationEmailSent"),
    CountryUpdated(4, "CountryUpdated"),
    DeactiveAccount(5, "DeactiveAccount"),
    ReactiveAccount(6, "ReactiveAccount"),
    FlagForDelection(7, "FlagForDelection"),

    UnknowAction(-1, "UnknowAction");

    private Integer id;
    private String name;

    CsrLogActionType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

}
