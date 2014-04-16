/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;


import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Service.
 */
@Transactional
public interface EntitlementDefinitionService {
    EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId);

    List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String clientId, String group, String tag,
                                                          String type, PageableGetOptions pageMetadata);

    Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition);

    EntitlementType getEntitlementType(String name);

    EntitlementDefinition getByTrackingUuid(UUID trackingUuid);
}
