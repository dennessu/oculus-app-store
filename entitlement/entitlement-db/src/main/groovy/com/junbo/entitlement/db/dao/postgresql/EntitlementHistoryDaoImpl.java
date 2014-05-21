/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.postgresql;

import com.junbo.entitlement.db.dao.EntitlementHistoryDao;
import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;
import org.springframework.stereotype.Component;

/**
 * Hibernate Impl of EntitlementHistory Dao.
 */
@Component
public class EntitlementHistoryDaoImpl extends BaseDao<EntitlementHistoryEntity> implements EntitlementHistoryDao {
    @Override
    public EntitlementHistoryEntity insert(EntitlementHistoryEntity entitlementHistoryEntity) {
        entitlementHistoryEntity.setpId(generateId(entitlementHistoryEntity.getShardMasterId()));
        return get((Long) currentSession(entitlementHistoryEntity.getShardMasterId()).save(entitlementHistoryEntity));
    }
}