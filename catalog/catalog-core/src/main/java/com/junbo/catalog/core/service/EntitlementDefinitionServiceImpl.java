/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.db.repo.EntitlementDefinitionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Service for EntitlementDefinition.
 */
public class EntitlementDefinitionServiceImpl implements EntitlementDefinitionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementDefinitionService.class);
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;

    @Override
    public EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId) {
        EntitlementDefinition entitlementDefinition = entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (entitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinition;
    }

    @Override
    public List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String clientId,
                                                                 String group, String tag,
                                                                 String type, PageableGetOptions pageMetadata) {
        checkDeveloper(developerId);
        return entitlementDefinitionRepository.getByParams(developerId, clientId, group, tag, type, pageMetadata);
    }

    @Override
    public Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition) {
        validateNotNull(entitlementDefinition.getType(), "type");
        if(entitlementDefinition.getGroup() == null){
            entitlementDefinition.setGroup("");
        }
        if(entitlementDefinition.getTag() == null){
            entitlementDefinition.setTag("");
        }
        if(entitlementDefinition.getConsumable() == null){
            entitlementDefinition.setConsumable(false);
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        checkInAppContext(entitlementDefinition.getInAppContext());
        return entitlementDefinitionRepository.create(entitlementDefinition);
    }

    private void checkInAppContext(List<String> inAppContext) {
    }

    @Override
    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementDefinitionRepository.getByTrackingUuid(trackingUuid);
    }

    protected void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw AppErrors.INSTANCE.missingField(fieldName).exception();
        }
    }

    private void checkDeveloper(Long developerId) {
    }
}
