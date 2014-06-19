/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant;

import com.junbo.billing.db.repo.BalanceItemEventRepository;
import com.junbo.billing.spec.model.BalanceItemEvent;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

/**
 * Created by haomin on 14-6-19.
 */
public class BalanceItemEventRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<BalanceItemEvent, Long> implements BalanceItemEventRepository {
}
