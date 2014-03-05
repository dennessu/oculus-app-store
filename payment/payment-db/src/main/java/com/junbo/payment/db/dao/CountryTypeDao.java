/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.payment.db.entity.CountryTypeEntity;

/**
 * country dao.
 */
public class CountryTypeDao extends GenericDAOImpl<CountryTypeEntity, Integer> {
    public CountryTypeDao(){
        super(CountryTypeEntity.class);
    }
}
