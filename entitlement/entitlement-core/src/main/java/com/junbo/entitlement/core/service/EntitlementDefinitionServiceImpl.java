/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.entitlement.common.exception.FieldNotMatchException;
import com.junbo.entitlement.common.exception.MissingFieldException;
import com.junbo.entitlement.common.exception.NotFoundException;
import com.junbo.entitlement.core.EntitlementDefinitionService;
import com.junbo.entitlement.db.repository.EntitlementDefinitionRepository;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for EntitlementDefinition.
 */
public class EntitlementDefinitionServiceImpl extends BaseService implements EntitlementDefinitionService {
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;

    @Override
    @Transactional
    public EntitlementDefinition getEntitlementDefinition(Long entitlementDefinitionId) {
        EntitlementDefinition entitlementDefinition = entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (entitlementDefinition == null) {
            throw new NotFoundException("entitlementDefinition", entitlementDefinitionId);
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinition;
    }

    @Override
    @Transactional
    public List<EntitlementDefinition> getEntitlementDefinitions(Long developerId, String group, String tag,
                                                                 String type, PageMetadata pageMetadata) {
        if (developerId == null) {
            throw new MissingFieldException("developerId");
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
        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);

        if (existingEntitlementDefinition == null) {
            throw new NotFoundException("entitlementDefinition", entitlementDefinitionId);
        }

        checkDeveloper(existingEntitlementDefinition.getDeveloperId());

        if (!existingEntitlementDefinition.getDeveloperId().equals(entitlementDefinition.getDeveloperId())) {
            throw new FieldNotMatchException("developerId", entitlementDefinition.getDeveloperId().toString(),
                    existingEntitlementDefinition.getDeveloperId().toString());
        }

        existingEntitlementDefinition.setTag(entitlementDefinition.getTag());
        existingEntitlementDefinition.setGroup(entitlementDefinition.getGroup());
        existingEntitlementDefinition.setType(entitlementDefinition.getType());

        return entitlementDefinitionRepository.update(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public void deleteEntitlement(Long entitlementDefinitionId) {
        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);
        if (existingEntitlementDefinition == null) {
            throw new NotFoundException("entitlementDefinition", entitlementDefinitionId);
        }
        checkDeveloper(existingEntitlementDefinition.getDeveloperId());
        entitlementDefinitionRepository.delete(existingEntitlementDefinition);
    }

    @Override
    @Transactional
    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementDefinitionRepository.getByTrackingUuid(trackingUuid);
    }
}
