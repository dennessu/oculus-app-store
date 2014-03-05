/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core;

import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;

import java.util.List;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Service.
 */
public interface EntitlementDefinitionService {
    EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId);

    List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String group, String tag,
                                                          String type, PageMetadata pageMetadata);

    EntitlementDefinition addEntitlementDefinition(EntitlementDefinition entitlementDefinition);

    EntitlementDefinition updateEntitlementDefinition(Long entitlementDefinitionId,
                                                      EntitlementDefinition entitlementDefinition);

    void deleteEntitlement(Long entitlementDefinitionId);

    EntitlementDefinition getByTrackingUuid(UUID trackingUuid);
}
