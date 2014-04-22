/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;


import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Service.
 */
@Transactional
public interface EntitlementDefinitionService {
    EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId);

    List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String clientId, Set<String> groups, Set<String> tags,
                                                          Set<String> types, Boolean isConsumable, PageableGetOptions pageMetadata);

    Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition);

    Long updateEntitlementDefinition(Long entitlementDefinitionId,
                                     EntitlementDefinition entitlementDefinition);

    void deleteEntitlement(Long entitlementDefinitionId);


    EntitlementDefinition getByTrackingUuid(UUID trackingUuid);
}
