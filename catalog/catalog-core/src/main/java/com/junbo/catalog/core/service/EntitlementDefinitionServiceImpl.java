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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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
                                                                 Set<String> groups, Set<String> tags, String type,
                                                                 Boolean isConsumable, PageableGetOptions pageMetadata) {
        checkDeveloper(developerId);
        return entitlementDefinitionRepository.getByParams(developerId, clientId, groups, tags, type, isConsumable, pageMetadata);
    }

    @Override
    public Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getGroup() == null) {
            entitlementDefinition.setGroup("");
        }
        if (entitlementDefinition.getTag() == null) {
            entitlementDefinition.setTag("");
        }
        if (entitlementDefinition.getConsumable() == null) {
            entitlementDefinition.setConsumable(false);
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        checkInAppContext(entitlementDefinition.getInAppContext());
        return entitlementDefinitionRepository.create(entitlementDefinition);
    }

    private void checkInAppContext(List<String> inAppContext) {
    }

    @Override
    @Transactional
    public Long updateEntitlementDefinition(Long entitlementDefinitionId,
                                            EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getEntitlementDefId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementDefinitionId.equals(entitlementDefinition.getEntitlementDefId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entitlementDefinition.getEntitlementDefId(),
                    entitlementDefinitionId).exception();
        }

        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }

        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        checkInAppContext(entitlementDefinition.getInAppContext());

        if (!existingEntitlementDefinition.getDeveloperId().equals(entitlementDefinition.getDeveloperId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("developer",
                    entitlementDefinition.getDeveloperId(),
                    existingEntitlementDefinition.getDeveloperId())
                    .exception();
        }

        existingEntitlementDefinition.setTag(entitlementDefinition.getTag());
        existingEntitlementDefinition.setGroup(entitlementDefinition.getGroup());
        existingEntitlementDefinition.setType(entitlementDefinition.getType());
        existingEntitlementDefinition.setConsumable(entitlementDefinition.getConsumable());
        existingEntitlementDefinition.setInAppContext(entitlementDefinition.getInAppContext());

        return entitlementDefinitionRepository.update(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public void deleteEntitlement(Long entitlementDefinitionId) {
        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }
        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        entitlementDefinitionRepository.delete(existingEntitlementDefinition);
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
