/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo;

import com.junbo.catalog.db.dao.EntitlementDefinitionDao;
import com.junbo.catalog.db.entity.EntitlementDefinitionEntity;
import com.junbo.catalog.db.mapper.EntitlementDefinitionMapper;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Repository of EntitlementDefinition.
 */
public class EntitlementDefinitionRepository {
    @Autowired
    private EntitlementDefinitionDao entitlementDefinitionDao;

    public EntitlementDefinition get(Long entitlementDefinitionId) {
        return EntitlementDefinitionMapper.toEntitlementDefinition(
                entitlementDefinitionDao.get(entitlementDefinitionId));
    }

    public Long create(EntitlementDefinition entitlementDefinition) {
        return entitlementDefinitionDao.create(
                EntitlementDefinitionMapper.toEntitlementDefinitionEntity(entitlementDefinition));
    }

    public Long update(EntitlementDefinition entitlementDefinition) {

        return entitlementDefinitionDao.update(
                EntitlementDefinitionMapper.toEntitlementDefinitionEntity(entitlementDefinition));
    }

    public void delete(EntitlementDefinition entitlementDefinition) {
        EntitlementDefinitionEntity entitlementDefinitionEntity =
                EntitlementDefinitionMapper.toEntitlementDefinitionEntity(entitlementDefinition);
        entitlementDefinitionEntity.setDeleted(true);
        entitlementDefinitionDao.update(entitlementDefinitionEntity);
    }


    public List<EntitlementDefinition> getByParams(Long developerId, String clientId,
                                                   Set<String> groups, Set<String> tags, Set<EntitlementType> types,
                                                   Boolean isConsumable, PageableGetOptions pageMetadata) {
        return EntitlementDefinitionMapper.toEntitlementDefinitionList(
                entitlementDefinitionDao.getByParams(developerId, clientId, groups, tags, types, isConsumable,
                        pageMetadata == null ? new PageableGetOptions().ensurePagingValid() : pageMetadata));
    }

    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return EntitlementDefinitionMapper.toEntitlementDefinition(
                entitlementDefinitionDao.getByTrackingUuid(trackingUuid));
    }
}
