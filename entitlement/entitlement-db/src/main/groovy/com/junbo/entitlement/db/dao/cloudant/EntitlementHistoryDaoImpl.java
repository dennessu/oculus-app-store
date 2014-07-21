/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;

/**
 * cloudantImpl of entitlementHistoryDao.
 */
public class EntitlementHistoryDaoImpl extends CloudantClient<EntitlementHistoryEntity> implements EntitlementHistoryDao {

    @Override
    public EntitlementHistoryEntity insert(EntitlementHistoryEntity entitlementHistory) {
        return cloudantPostSync(entitlementHistory);
    }
}
