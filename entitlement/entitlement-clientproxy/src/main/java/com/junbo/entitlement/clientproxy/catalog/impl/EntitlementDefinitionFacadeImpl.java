/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.resource.proxy.EntitlementDefinitionResourceClientProxy;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.entitlement.clientproxy.catalog.EntitlementDefinitionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Impl of CatalogFacade.
 */
public class EntitlementDefinitionFacadeImpl implements EntitlementDefinitionFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementDefinitionFacadeImpl.class);
    @Autowired
    @Qualifier("definitionClient")
    private EntitlementDefinitionResourceClientProxy definitionClientProxy;

    @Override
    public EntitlementDefinition getDefinition(Long definitionId) {
        try {
            LOGGER.info("Getting entitlementDefinition [{}] started.", definitionId);
            EntitlementDefinition definition = definitionClientProxy.getEntitlementDefinition(
                    new EntitlementDefinitionId(definitionId)).wrapped().get();
            LOGGER.info("Getting entitlementDefinition [{}] finished.", definitionId);
            return definition;
        } catch (Exception e) {
            LOGGER.error("Getting entitlementDefinition [{" + definitionId + "}] failed.", e);
            return null;
        }
    }
}
