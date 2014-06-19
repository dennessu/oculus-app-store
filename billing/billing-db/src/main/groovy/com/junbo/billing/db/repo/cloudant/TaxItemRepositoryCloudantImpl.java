/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant;

import com.junbo.billing.db.repo.TaxItemRepository;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class TaxItemRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TaxItem, Long> implements TaxItemRepository {
    @Override
    public Promise<List<TaxItem>> findByBalanceItemId(Long balanceItemId) {
        return super.queryView("by_balance_item_id", balanceItemId.toString());
    }
}
