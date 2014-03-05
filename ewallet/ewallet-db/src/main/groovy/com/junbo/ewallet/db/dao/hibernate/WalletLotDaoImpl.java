/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;

/**
 * Hibernate impl of WalletLotDao.
 */
public class WalletLotDaoImpl extends BaseDao<WalletLotEntity> implements WalletLotDao{
    @Override
    public void delete(Long id) {
        currentSession().delete(get(id));
    }
}
