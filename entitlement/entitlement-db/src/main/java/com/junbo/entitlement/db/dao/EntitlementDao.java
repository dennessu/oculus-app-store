/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao;

import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.def.EntitlementType;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Interface of Entitlement Dao.
 */
public interface EntitlementDao {
    EntitlementEntity insert(EntitlementEntity entitlement);

    EntitlementEntity get(Long entitlementId);

    EntitlementEntity update(EntitlementEntity entitlement);

    List<EntitlementEntity> getBySearchParam(EntitlementSearchParam entitlementSearchParam,
                                             PageMetadata pageMetadata);

    EntitlementEntity getByTrackingUuid(UUID trackingUuid);

    EntitlementEntity getExistingManagedEntitlement(Long userId, Long definitionId);

    EntitlementEntity getExistingManagedEntitlement(
            Long userId, EntitlementType type, Long developerId, String group, String tag);
}
