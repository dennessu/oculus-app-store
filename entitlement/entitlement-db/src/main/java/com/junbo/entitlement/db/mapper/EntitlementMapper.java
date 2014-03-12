/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.mapper;

import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.dao.EntitlementDefinitionDao;
import com.junbo.entitlement.db.entity.EntitlementDefinitionEntity;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mapper for entitlement.
 */
@Component
public class EntitlementMapper {
    @Autowired
    private EntitlementDefinitionDao entitlementDefinitionDao;

    public Entitlement toEntitlement(EntitlementEntity entitlementEntity) {
        if (entitlementEntity == null) {
            return null;
        }
        Entitlement entitlement = new Entitlement();
        entitlement.setEntitlementId(entitlementEntity.getEntitlementId());
        entitlement.setUserId(entitlementEntity.getUserId());
        entitlement.setType(entitlementEntity.getType().toString());
        entitlement.setGroup(entitlementEntity.getGroup());
        entitlement.setTag(entitlementEntity.getTag());
        entitlement.setDeveloperId(entitlementEntity.getDeveloperId());
        entitlement.setEntitlementDefinitionId(entitlementEntity.getEntitlementDefinitionId());
        entitlement.setStatus(getStatus(entitlementEntity).toString());
        entitlement.setStatusReason(entitlementEntity.getStatusReason());
        entitlement.setOfferId(entitlementEntity.getOfferId());
        entitlement.setGrantTime(entitlementEntity.getGrantTime());
        entitlement.setExpirationTime(entitlementEntity.getExpirationTime());
        entitlement.setConsumable(entitlementEntity.getConsumable());
        entitlement.setUseCount(entitlementEntity.getUseCount());
        entitlement.setManagedLifecycle(entitlementEntity.getManagedLifecycle());
        return entitlement;
    }

    public EntitlementEntity toEntitlementEntity(Entitlement entitlement) {
        EntitlementEntity entitlementEntity = new EntitlementEntity();
        entitlementEntity.setTrackingUuid(entitlement.getTrackingUuid());
        entitlementEntity.setEntitlementId(entitlement.getEntitlementId());

        EntitlementDefinitionEntity definitionEntity =
                entitlementDefinitionDao.get(entitlement.getEntitlementDefinitionId());
        entitlementEntity.setEntitlementDefinitionId(
                definitionEntity.getEntitlementDefinitionId());
        entitlementEntity.setDeveloperId(definitionEntity.getDeveloperId());
        entitlementEntity.setType(definitionEntity.getType());
        entitlementEntity.setGroup(definitionEntity.getGroup());
        entitlementEntity.setTag(definitionEntity.getTag());

        entitlementEntity.setUserId(entitlement.getUserId());
        entitlementEntity.setManagedLifecycle(entitlement.getManagedLifecycle());
        entitlementEntity.setStatus(getStatus(entitlement));
        entitlementEntity.setStatusReason(entitlement.getStatusReason());
        entitlementEntity.setOfferId(entitlement.getOfferId());
        entitlementEntity.setGrantTime(entitlement.getGrantTime());
        entitlementEntity.setExpirationTime(entitlement.getExpirationTime());
        entitlementEntity.setConsumable(entitlement.getConsumable());
        entitlementEntity.setUseCount(entitlement.getUseCount());
        return entitlementEntity;
    }


    public List<Entitlement> toEntitlementList(List<EntitlementEntity> entitlementEntities) {
        List<Entitlement> entitlements = new ArrayList<Entitlement>(entitlementEntities.size());
        for (EntitlementEntity entitlementEntity : entitlementEntities) {
            entitlements.add(toEntitlement(entitlementEntity));
        }
        return entitlements;
    }

    public EntitlementDefinition toEntitlementDefinition(
            EntitlementDefinitionEntity entitlementDefinitionEntity) {
        if (entitlementDefinitionEntity == null) {
            return null;
        }
        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setEntitlementDefinitionId(
                entitlementDefinitionEntity.getEntitlementDefinitionId());
        entitlementDefinition.setDeveloperId(entitlementDefinitionEntity.getDeveloperId());
        entitlementDefinition.setType(entitlementDefinitionEntity.getType().toString());
        entitlementDefinition.setGroup(entitlementDefinitionEntity.getGroup());
        entitlementDefinition.setTag(entitlementDefinitionEntity.getTag());
        return entitlementDefinition;
    }

    public EntitlementDefinitionEntity toEntitlementDefinitionEntity(
            EntitlementDefinition entitlementDefinition) {
        EntitlementDefinitionEntity entitlementDefinitionEntity = new EntitlementDefinitionEntity();
        entitlementDefinitionEntity.setTrackingUuid(entitlementDefinition.getTrackingUuid());
        entitlementDefinitionEntity.setEntitlementDefinitionId(
                entitlementDefinition.getEntitlementDefinitionId());
        entitlementDefinitionEntity.setDeveloperId(entitlementDefinition.getDeveloperId());
        entitlementDefinitionEntity.setType(EntitlementType.valueOf(entitlementDefinition.getType()));
        entitlementDefinitionEntity.setGroup(entitlementDefinition.getGroup());
        entitlementDefinitionEntity.setTag(entitlementDefinition.getTag());
        return entitlementDefinitionEntity;
    }

    public List<EntitlementDefinition> toEntitlementDefinitionList(
            List<EntitlementDefinitionEntity> entitlementDefinitionEntities) {
        List<EntitlementDefinition> entitlementDefinitions =
                new ArrayList<EntitlementDefinition>(entitlementDefinitionEntities.size());
        for (EntitlementDefinitionEntity entitlementDefinitionEntity : entitlementDefinitionEntities) {
            entitlementDefinitions.add(toEntitlementDefinition(entitlementDefinitionEntity));
        }
        return entitlementDefinitions;
    }

    private EntitlementStatus getStatus(EntitlementEntity entitlementEntity) {
        if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS
                .contains(entitlementEntity.getStatus())) {
            return entitlementEntity.getStatus();
        }
        if (entitlementEntity.getManagedLifecycle() != null
                && entitlementEntity.getManagedLifecycle()) {
            Date now = EntitlementContext.current().getNow();
            if (entitlementEntity.getConsumable()
                    && entitlementEntity.getUseCount() < 1) {
                return EntitlementStatus.DISABLED;
            } else if (now.before(entitlementEntity.getGrantTime())) {
                return EntitlementStatus.PENDING;
            } else if (now.before(entitlementEntity.getExpirationTime())) {
                return EntitlementStatus.ACTIVE;
            } else {
                return EntitlementStatus.DISABLED;
            }
        }
        return entitlementEntity.getStatus();
    }

    private EntitlementStatus getStatus(Entitlement entitlement) {
        if (entitlement.getManagedLifecycle()) {
            return EntitlementStatus.MANAGED;
        }
        return EntitlementStatus.valueOf(entitlement.getStatus());
    }
}
