/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.paymentinstrument;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.paymentinstrument.AddressEntity;

/**
 * address dao.
 */
public class AddressDao extends CommonDataDAOImpl<AddressEntity, Long> {

    public AddressDao() {
        super(AddressEntity.class);
    }
}
