/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core;


import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;

import java.util.List;
import java.util.UUID;

/**
 * Interface of EntitlementDefinition Service.
 */
public interface EntitlementDefinitionService {
    EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId);

    List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String group, String tag,
                                                          String type, PageableGetOptions pageMetadata);

    Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition);

    EntitlementDefinition getByTrackingUuid(UUID trackingUuid);
}
