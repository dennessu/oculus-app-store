/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao;

import com.junbo.entitlement.db.entity.EntitlementDefinitionEntity;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Dao.
 */
public interface EntitlementDefinitionDao {
    EntitlementDefinitionEntity insert(EntitlementDefinitionEntity entitlementDefinition);

    EntitlementDefinitionEntity get(Long entitlementDefinitionId);

    EntitlementDefinitionEntity update(EntitlementDefinitionEntity entitlementDefinition);

    List<EntitlementDefinitionEntity> getByParams(Long developerId,
                                                  String group, String tag,
                                                  EntitlementType type, PageMetadata pageMetadata);

    EntitlementDefinitionEntity getByTrackingUuid(UUID trackingUuid);
}
