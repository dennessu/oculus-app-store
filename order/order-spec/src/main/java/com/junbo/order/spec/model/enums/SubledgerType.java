/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by fzhang on 4/2/2014.
 */
public enum SubledgerType implements Identifiable<Short> {

    PAYOUT(0, PayoutActionType.ADD),
    REFUND(1, PayoutActionType.DEDUCT),
    CHARGE_BACK(2, PayoutActionType.DEDUCT),
    DECLINE(3, PayoutActionType.NONE),
    CHARGE_BACK_REFUNDED(4, PayoutActionType.NONE),
    CHARGE_BACK_OTW(5, PayoutActionType.NONE), // charge back outside of time window
    CHARGE_BACK_REVERSAL(6, PayoutActionType.ADD),
    CHARGE_BACK_REVERSAL_OTW(7, PayoutActionType.NONE); // charge back reversal outside of time window

    /**
     * The PayoutActionType enum.
     */
    public enum PayoutActionType {
        ADD,
        DEDUCT,
        NONE
    }

    private SubledgerType(int id, PayoutActionType payoutActionType) {
        this.id = (short) id;
        this.payoutActionType = payoutActionType;
    }

    private Short id;
    private PayoutActionType payoutActionType;

    @Override
    public Short getId() {
        return id;
    }

    public PayoutActionType getPayoutActionType() {
        return payoutActionType;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum SubledgerItemAction not settable");
    }


}
