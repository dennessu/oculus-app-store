/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.entity.EntitlementDefinitionEntity;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;

import java.util.List;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Dao.
 */
public interface EntitlementDefinitionDao {
    Long create(EntitlementDefinitionEntity entitlementDefinition);

    EntitlementDefinitionEntity get(Long entitlementDefinitionId);

    List<EntitlementDefinitionEntity> getByParams(Long developerId, String clientId,
                                                  String group, String tag, EntitlementType type,
                                                  Boolean isConsumable, PageableGetOptions pageableGetOptions);

    Long update(EntitlementDefinitionEntity entitlementDefinition);

    EntitlementDefinitionEntity getByTrackingUuid(UUID trackingUuid);
}
