/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao;

import com.junbo.entitlement.db.entity.EntitlementHistoryEntity;

/**
 * Interface of EntitlementHistory Dao.
 */
public interface EntitlementHistoryDao {
    void insertAsync(EntitlementHistoryEntity entitlementHistory);
}
