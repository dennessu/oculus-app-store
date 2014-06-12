/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo;

import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public interface BalanceItemRepository extends BaseRepository<BalanceItem, Long> {
    @ReadMethod
    Promise<List<BalanceItem>> findByBalanceId(Long balanceId);
}
