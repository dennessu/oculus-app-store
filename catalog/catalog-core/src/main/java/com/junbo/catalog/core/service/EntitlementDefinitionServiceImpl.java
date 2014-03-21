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
            //TODO: change to specified exception later
            throw new CatalogException("missing developerId");
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
    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementDefinitionRepository.getByTrackingUuid(trackingUuid);
    }

    private void checkDeveloper(Long developerId) {
    }
}
