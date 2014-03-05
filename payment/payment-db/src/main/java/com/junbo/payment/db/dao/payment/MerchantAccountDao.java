/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;


import com.junbo.payment.db.dao.GenericDAOImpl;
import com.junbo.payment.db.entity.payment.MerchantAccountEntity;

/**
 * merchant dao.
 */
public class MerchantAccountDao extends GenericDAOImpl<MerchantAccountEntity, Integer> {
    public MerchantAccountDao() {
        super(MerchantAccountEntity.class);
    }
}
