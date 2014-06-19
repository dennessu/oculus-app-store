/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant;

import com.junbo.billing.db.repo.BalanceEventRepository;
import com.junbo.billing.spec.model.BalanceEvent;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

/**
 * Created by haomin on 14-6-19.
 */
public class BalanceEventRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<BalanceEvent, Long>
        implements BalanceEventRepository {
}
