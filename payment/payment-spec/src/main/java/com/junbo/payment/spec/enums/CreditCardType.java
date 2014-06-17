/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * credit credit type enum.
 */
public enum CreditCardType implements Identifiable<Short> {
    VISA((short)1),
    AMEX((short)2),
    CARTE_BLANCHE((short)3),
    CHINA_UNION_PAY((short)4),
    DINERS_CLUB_INTERNATIONAL((short)5),
    DISCOVER((short)6),
    JCB((short)7),
    LASER((short)8),
    MAESTRO((short)9),
    MASTER_CARD((short)10),
    SOLO((short)11),
    SWITCH((short)12);

    private final Short id;

    CreditCardType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return this.id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum CreditCardType not settable");
    }
}
