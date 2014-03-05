/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.payment.db.entity.CurrencyTypeEntity;

/**
 * currency dao.
 */
public class CurrencyTypeDao extends GenericDAOImpl<CurrencyTypeEntity, Integer> {
    public CurrencyTypeDao() {
            super(CurrencyTypeEntity.class);
    }
}
