/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.CurrencyEntity;

import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
public interface CurrencyEntityDao extends BaseDao<CurrencyEntity, String> {

    List<CurrencyEntity> loadAll();

}
