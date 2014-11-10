/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.util.Context;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import com.junbo.langur.core.promise.Promise;

/**
 * cloudantImpl of entitlementHistoryDao.
 */
public class EntitlementHistoryDaoImpl extends CloudantClient<EntitlementHistoryEntity> implements EntitlementHistoryDao {

    @Override
    public void insertAsync(final EntitlementHistoryEntity entitlementHistory) {
        Context.get().registerPendingTask(new Promise.Func0<Promise>() {
            @Override
            public Promise apply() {
                return cloudantPost(entitlementHistory);
            }
        });
    }
}
