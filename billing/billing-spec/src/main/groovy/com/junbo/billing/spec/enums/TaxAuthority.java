/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by xmchen on 14-2-17.
 */
public enum TaxAuthority implements Identifiable<Short> {
    COUNTRY((short)1),
    STATE((short)2),
    COUNTY((short)3),
    CITY((short)4),
    STATE2((short)5),
    COUNTY2((short)6),
    CITY2((short)7),
    DISTRICT((short)8),
    TERRITORY((short)9),
    PST((short)10),
    GST((short)11),
    VAT((short)12),
    QST((short)13),
    HST((short)14),

    UNKNOWN((short)999);

    private final Short id;

    TaxAuthority(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }
}
