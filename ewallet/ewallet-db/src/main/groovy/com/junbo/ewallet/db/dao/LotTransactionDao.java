/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.hibernate.LotTransactionEntity;

/**
 * Interface of LotTransactionDao.
 */
public interface LotTransactionDao {
    LotTransactionEntity get(Long id);

    Long insert(LotTransactionEntity lotTransactionEntity);
}
