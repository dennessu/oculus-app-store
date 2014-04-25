/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.mapper;

import com.junbo.catalog.db.entity.EntitlementDefinitionEntity;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for entitlement.
 */
@Component
public class EntitlementDefinitionMapper {
    private EntitlementDefinitionMapper() {
    }

    public static EntitlementDefinition toEntitlementDefinition(
            EntitlementDefinitionEntity entitlementDefinitionEntity) {
        if (entitlementDefinitionEntity == null) {
            return null;
        }
        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setEntitlementDefId(
                entitlementDefinitionEntity.getEntitlementDefinitionId());
        entitlementDefinition.setDeveloperId(entitlementDefinitionEntity.getDeveloperId());
        entitlementDefinition.setRev(entitlementDefinitionEntity.getRev().toString());
        entitlementDefinition.setInAppContext(entitlementDefinitionEntity.getInAppContext());
        entitlementDefinition.setType(
                entitlementDefinitionEntity.getType() == null ?
                        null :
                        entitlementDefinitionEntity.getType().toString());
        entitlementDefinition.setGroup(entitlementDefinitionEntity.getGroup());
        entitlementDefinition.setTag(entitlementDefinitionEntity.getTag());
        entitlementDefinition.setConsumable(entitlementDefinitionEntity.getConsumable());
        return entitlementDefinition;
    }

    public static EntitlementDefinitionEntity toEntitlementDefinitionEntity(
            EntitlementDefinition entitlementDefinition) {
        EntitlementDefinitionEntity entitlementDefinitionEntity = new EntitlementDefinitionEntity();
        entitlementDefinitionEntity.setTrackingUuid(entitlementDefinition.getTrackingUuid());
        entitlementDefinitionEntity.setEntitlementDefinitionId(
                entitlementDefinition.getEntitlementDefId());
        if (entitlementDefinition.getRev() != null) {
            entitlementDefinitionEntity.setRev(Integer.valueOf(entitlementDefinition.getRev()));
        }
        entitlementDefinitionEntity.setInAppContext(entitlementDefinition.getInAppContext());
        entitlementDefinitionEntity.setDeveloperId(entitlementDefinition.getDeveloperId());
        entitlementDefinitionEntity.setType(
                StringUtils.isEmpty(entitlementDefinition.getType()) ?
                        null :
                        EntitlementType.valueOf(entitlementDefinition.getType())
        );
        entitlementDefinitionEntity.setGroup(entitlementDefinition.getGroup());
        entitlementDefinitionEntity.setTag(entitlementDefinition.getTag());
        entitlementDefinitionEntity.setConsumable(entitlementDefinition.getConsumable());
        return entitlementDefinitionEntity;
    }

    public static List<EntitlementDefinition> toEntitlementDefinitionList(
            List<EntitlementDefinitionEntity> entitlementDefinitionEntities) {
        List<EntitlementDefinition> entitlementDefinitions =
                new ArrayList<EntitlementDefinition>(entitlementDefinitionEntities.size());
        for (EntitlementDefinitionEntity entitlementDefinitionEntity : entitlementDefinitionEntities) {
            entitlementDefinitions.add(toEntitlementDefinition(entitlementDefinitionEntity));
        }
        return entitlementDefinitions;
    }
}
