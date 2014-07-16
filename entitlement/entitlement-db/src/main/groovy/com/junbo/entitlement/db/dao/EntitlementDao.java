/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao;

import com.junbo.common.model.Results;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.UUID;

/**
 * Interface of Entitlement Dao.
 */
public interface EntitlementDao {
    EntitlementEntity insert(EntitlementEntity entitlement);

    EntitlementEntity get(String entitlementId);

    EntitlementEntity update(EntitlementEntity entitlement, EntitlementEntity oldEntitlement);

    Results<EntitlementEntity> getBySearchParam(EntitlementSearchParam entitlementSearchParam,
                                             PageMetadata pageMetadata);

    EntitlementEntity getByTrackingUuid(Long shardMasterId, UUID trackingUuid);

    EntitlementEntity get(Long userId, String itemId, String type);
}
