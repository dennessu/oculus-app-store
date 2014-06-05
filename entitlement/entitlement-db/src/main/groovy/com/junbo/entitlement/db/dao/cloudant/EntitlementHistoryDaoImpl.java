/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.cloudant;

import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * cloudantImpl of entitlementHistoryDao.
 */
public class EntitlementHistoryDaoImpl extends CloudantClient<EntitlementHistoryEntity> implements EntitlementHistoryDao {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null;
    }

    @Override
    public EntitlementHistoryEntity insert(EntitlementHistoryEntity entitlementHistory) {
        entitlementHistory.setpId(generateId(entitlementHistory.getShardMasterId()));
        return super.cloudantPost(entitlementHistory).get();
    }
}
