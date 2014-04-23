/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefSearchParams;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;

import java.util.List;

/**
 * Interface wrapper to call from catalog.
 */
public interface EntitlementDefinitionFacade {
    EntitlementDefinition getDefinition(Long definitionId);
    List<EntitlementDefinition> getDefinitions(EntitlementDefSearchParams params);
}
