/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.csr.spec.def;

import com.junbo.common.util.Identifiable;
import javax.ws.rs.NotSupportedException;

/**
 * enum for TransactionType.
 */
public enum CsrLogActionType implements Identifiable<Integer> {
    TroubleshootResolved(0),
    RefundIssued(1),
    ResetPasswordEmailSent(2),
    VerficationEmailSent(3),
    ExemptionApproved(4),


    UnknowAction(-1);

    private Integer id;

    CsrLogActionType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        throw new NotSupportedException("enum CsrLogActionType not settable");
    }
}
