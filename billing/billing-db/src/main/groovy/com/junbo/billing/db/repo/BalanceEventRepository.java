/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo;

import com.junbo.billing.spec.model.BalanceEvent;
import com.junbo.sharding.repo.BaseRepository;

/**
 * Created by haomin on 14-6-9.
 */
public interface BalanceEventRepository extends BaseRepository<BalanceEvent, Long> {
}
