/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.entitlement.core.EntitlementDefinitionService;
import com.junbo.entitlement.db.repository.EntitlementDefinitionRepository;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for EntitlementDefinition.
 */
public class EntitlementDefinitionServiceImpl extends BaseService implements EntitlementDefinitionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementDefinitionService.class);
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;
    @Autowired
    private EntitlementRepository entitlementRepository;

    @Override
    @Transactional
    public EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId) {
        EntitlementDefinition entitlementDefinition = entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (entitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinition;
    }

    @Override
    @Transactional
    public List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String group, String tag,
                                                                 String type, PageMetadata pageMetadata) {
        if (developerId == null) {
            throw AppErrors.INSTANCE.missingField("developerId").exception();
        }
        checkDeveloper(developerId);
        return entitlementDefinitionRepository.getByParams(developerId, group, tag, type, pageMetadata);
    }

    @Override
    @Transactional
    public EntitlementDefinition addEntitlementDefinition(EntitlementDefinition entitlementDefinition) {
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinitionRepository.insert(entitlementDefinition);
    }

    @Override
    @Transactional
    public EntitlementDefinition updateEntitlementDefinition(Long entitlementDefinitionId,
                                                             EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getEntitlementDefinitionId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementDefinitionId.equals(entitlementDefinition.getEntitlementDefinitionId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entitlementDefinition.getEntitlementDefinitionId().toString(),
                    entitlementDefinitionId.toString()).exception();
        }

        checkUnUsed(entitlementDefinitionId);

        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);

        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }

        checkDeveloper(existingEntitlementDefinition.getDeveloperId());

        if (!existingEntitlementDefinition.getDeveloperId().equals(entitlementDefinition.getDeveloperId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("developerId", entitlementDefinition.getDeveloperId().toString(),
                    existingEntitlementDefinition.getDeveloperId().toString()).exception();
        }

        existingEntitlementDefinition.setTag(entitlementDefinition.getTag());
        existingEntitlementDefinition.setGroup(entitlementDefinition.getGroup());
        existingEntitlementDefinition.setType(entitlementDefinition.getType());

        return entitlementDefinitionRepository.update(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public void deleteEntitlement(Long entitlementDefinitionId) {
        checkUnUsed(entitlementDefinitionId);
        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw AppErrors.INSTANCE.notFound("entitlementDefinition", entitlementDefinitionId).exception();
        }
        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        entitlementDefinitionRepository.delete(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementDefinitionRepository.getByTrackingUuid(trackingUuid);
    }

    private void checkUnUsed(Long entitlementDefinitionId) {
        if (entitlementRepository.existWithEntitlementDefinition(entitlementDefinitionId)) {
            LOGGER.error("Try to modify used entitlementDefinition [{}]" +
                    " which is not permitted.", entitlementDefinitionId);
            throw AppErrors.INSTANCE.common("entitlementDefinition [" +
                    entitlementDefinitionId +
                    "] can not be modified." +
                    " There have been entitlements with the entitlementDefinition").exception();
        }
    }
}
