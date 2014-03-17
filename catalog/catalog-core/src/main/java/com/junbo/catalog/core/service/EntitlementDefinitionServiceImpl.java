/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.common.exception.CatalogException;
import com.junbo.catalog.common.exception.NotFoundException;
import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.db.repo.EntitlementDefinitionRepository;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
                                                                 String type, PageableGetOptions pageMetadata) {
        if (developerId == null) {
            throw new CatalogException("missing developerId");  //TODO: change to specified exception later
        }
        checkDeveloper(developerId);
        return entitlementDefinitionRepository.getByParams(developerId, group, tag, type, pageMetadata);
    }

    @Override
    @Transactional
    public Long createEntitlementDefinition(EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getType() == null) {
            entitlementDefinition.setType(EntitlementType.DEFAULT.toString());
        }
        checkDeveloper(entitlementDefinition.getDeveloperId());
        return entitlementDefinitionRepository.create(entitlementDefinition);
    }

    @Override
    @Transactional
    //for now definition can not be updated.
    public Long updateEntitlementDefinition(Long entitlementDefinitionId,
                                                             EntitlementDefinition entitlementDefinition) {
        if (entitlementDefinition.getEntitlementDefinitionId() == null) {
            throw new CatalogException("missing id");  //TODO: change to specified exception later
        }
        if (!entitlementDefinitionId.equals(entitlementDefinition.getEntitlementDefinitionId())) {
            throw new CatalogException("id not match");  //TODO: change to specified exception later
        }

        if (entitlementDefinition.getType() == null) {
            entitlementDefinition.setType(EntitlementType.DEFAULT.toString());
        }

        EntitlementDefinition existingEntitlementDefinition =
                entitlementDefinitionRepository.get(entitlementDefinitionId);

        if (existingEntitlementDefinition == null) {
            throw new NotFoundException("entitlementDefinition", entitlementDefinitionId);
        }

        checkDeveloper(existingEntitlementDefinition.getDeveloperId());

        if (!existingEntitlementDefinition.getDeveloperId().equals(entitlementDefinition.getDeveloperId())) {
            throw new CatalogException("not match");  //TODO: change to specified exception later
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

    private void checkDeveloper(Long developerId) {
    }
}
