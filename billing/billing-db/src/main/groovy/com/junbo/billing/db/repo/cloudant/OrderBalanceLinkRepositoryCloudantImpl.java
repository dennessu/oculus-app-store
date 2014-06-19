/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant;

import com.junbo.billing.db.repo.OrderBalanceLinkRepository;
import com.junbo.billing.spec.model.OrderBalanceLink;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class OrderBalanceLinkRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<OrderBalanceLink, Long> implements OrderBalanceLinkRepository {
    @Override
    public Promise<List<OrderBalanceLink>> findByOrderId(Long orderId) {
        return super.queryView("by_order_id", orderId.toString());
    }

    @Override
    public Promise<List<OrderBalanceLink>> findByBalanceId(Long balanceId) {
        return super.queryView("by_balance_id", balanceId.toString());
    }
}
