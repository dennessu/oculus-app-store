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
    SPECIAL((short)15),
    ESTIMATE((short)16),
    SERVICE((short)17),
    EDUCESS((short)18),
    SHECESS((short)19),
    IPI((short)20),
    PIS((short)21),
    COF((short)22),
    ICMS((short)23),
    ISS((short)24),
    BT((short)25),
    SURCHARGE((short)26),
    USWHT((short)27),
    BACKUPUSWHT((short)28),

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
