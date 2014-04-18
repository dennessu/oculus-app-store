/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.mapper;

import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.model.Entitlement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Mapper for entitlement.
 */
@Component
public class EntitlementMapper {
    public Entitlement toEntitlement(EntitlementEntity entitlementEntity) {
        if (entitlementEntity == null) {
            return null;
        }
        Entitlement entitlement = new Entitlement();
        entitlement.setEntitlementId(entitlementEntity.getEntitlementId());
        entitlement.setRev(entitlementEntity.getRev());
        entitlement.setUserId(entitlementEntity.getUserId());
        entitlement.setEntitlementDefinitionId(entitlementEntity.getEntitlementDefinitionId());
        entitlement.setIsActive(isActive(entitlementEntity));
        entitlement.setIsBanned(entitlementEntity.getIsBanned());
        entitlement.setGrantTime(entitlementEntity.getGrantTime());
        entitlement.setExpirationTime(entitlementEntity.getExpirationTime());
        entitlement.setUseCount(entitlementEntity.getUseCount());
        return entitlement;
    }

    public EntitlementEntity toEntitlementEntity(Entitlement entitlement) {
        EntitlementEntity entitlementEntity = new EntitlementEntity();
        entitlementEntity.setTrackingUuid(entitlement.getTrackingUuid());
        entitlementEntity.setEntitlementId(entitlement.getEntitlementId());
        entitlementEntity.setRev(entitlement.getRev());
        entitlementEntity.setEntitlementDefinitionId(
                entitlement.getEntitlementDefinitionId());
        entitlementEntity.setUserId(entitlement.getUserId());
        entitlementEntity.setIsBanned(entitlement.getIsBanned());
        entitlementEntity.setGrantTime(entitlement.getGrantTime());
        entitlementEntity.setExpirationTime(entitlement.getExpirationTime());
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

    private Boolean isActive(EntitlementEntity entitlementEntity) {
        if(entitlementEntity.getIsBanned()){
            return false;
        }
        Date now = EntitlementContext.current().getNow();
        Date expirationDate = entitlementEntity.getExpirationTime();
        Integer useCount = entitlementEntity.getUseCount();
        if (entitlementEntity.getGrantTime().before(now) &&
                (expirationDate == null || expirationDate.after(now)) &&
                (useCount == null || useCount > 0)) {
            return true;
        }
        return false;
    }
}
