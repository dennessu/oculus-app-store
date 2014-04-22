/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefSearchParams;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * Mocked definition facade.
 */
public class MockDefinitionFacadeImpl implements EntitlementDefinitionFacade {
    @Override
    public EntitlementDefinition getDefinition(Long definitionId) {
        return new EntitlementDefinition();
    }

    @Override
    public List<EntitlementDefinition> getDefinitions(EntitlementDefSearchParams params) {
        List<EntitlementDefinition> definitions = new ArrayList<>();
        return definitions;
    }
}
