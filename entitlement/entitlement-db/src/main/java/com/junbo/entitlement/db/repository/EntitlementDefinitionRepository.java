/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.repository;

import com.junbo.entitlement.db.dao.EntitlementDefinitionDao;
import com.junbo.entitlement.db.entity.EntitlementDefinitionEntity;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.mapper.EntitlementMapper;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Repository of EntitlementDefinition.
 */
public class EntitlementDefinitionRepository {
    @Autowired
    private EntitlementDefinitionDao entitlementDefinitionDao;
    @Autowired
    private EntitlementMapper entitlementMapper;

    public EntitlementDefinition get(Long entitlementDefinitionId) {
        return entitlementMapper.toEntitlementDefinition(
                entitlementDefinitionDao.get(entitlementDefinitionId));
    }

    public EntitlementDefinition insert(EntitlementDefinition entitlementDefinition) {
        return entitlementMapper.toEntitlementDefinition(entitlementDefinitionDao.insert(
                entitlementMapper.toEntitlementDefinitionEntity(entitlementDefinition)));
    }

    public EntitlementDefinition update(EntitlementDefinition entitlementDefinition) {
        return entitlementMapper.toEntitlementDefinition(entitlementDefinitionDao.update(
                entitlementMapper.toEntitlementDefinitionEntity(entitlementDefinition)));
    }

    public void delete(EntitlementDefinition entitlementDefinition) {
        EntitlementDefinitionEntity entitlementDefinitionEntity =
                entitlementMapper.toEntitlementDefinitionEntity(entitlementDefinition);
        entitlementDefinitionEntity.setStatus("DELETED");
        entitlementDefinitionDao.update(entitlementDefinitionEntity);
    }

    public List<EntitlementDefinition> getByParams(Long developerId,
                                                   String group, String tag,
                                                   String type, PageMetadata pageMetadata) {
        return entitlementMapper.toEntitlementDefinitionList(
                entitlementDefinitionDao.getByParams(developerId, group, tag,
                        StringUtils.isEmpty(type) ? null : EntitlementType.valueOf(type),
                        pageMetadata == null ? new PageMetadata() : pageMetadata));
    }

    public EntitlementDefinition getByTrackingUuid(UUID trackingUuid) {
        return entitlementMapper.toEntitlementDefinition(
                entitlementDefinitionDao.getByTrackingUuid(trackingUuid));
    }
}
