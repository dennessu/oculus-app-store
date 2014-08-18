/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.mapper;

import com.junbo.common.model.AdminInfo;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.def.EntitlementConsts;
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
        entitlement.setId(entitlementEntity.getId());
        entitlement.setRev(entitlementEntity.getRev());
        entitlement.setUserId(entitlementEntity.getUserId());
        entitlement.setItemId(entitlementEntity.getItemId());
        entitlement.setIsActive(isActive(entitlementEntity));
        entitlement.setIsBanned(entitlementEntity.getIsBanned());
        entitlement.setGrantTime(entitlementEntity.getGrantTime());
        entitlement.setFutureExpansion(entitlementEntity.getFutureExpansion());
        entitlement.setCreatedTime(entitlementEntity.getCreatedTime());
        entitlement.setUpdatedTime(entitlementEntity.getUpdatedTime());
        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setCreatedBy(entitlementEntity.getCreatedBy());
        adminInfo.setUpdatedBy(entitlementEntity.getUpdatedBy());
        entitlement.setAdminInfo(adminInfo);
        if (entitlementEntity.getExpirationTime().getTime() == EntitlementConsts.NEVER_EXPIRE.getTime()) {
            entitlement.setExpirationTime(null);
        } else {
            entitlement.setExpirationTime(entitlementEntity.getExpirationTime());
        }
        if (entitlementEntity.getUseCount().equals(EntitlementConsts.UNCONSUMABLE_USECOUNT)) {
            entitlement.setUseCount(null);
        } else {
            entitlement.setUseCount(entitlementEntity.getUseCount());
        }
        if (!entitlementEntity.getType().equalsIgnoreCase(EntitlementConsts.NO_TYPE)) {
            entitlement.setType(entitlementEntity.getType());
        }
        return entitlement;
    }

    public EntitlementEntity toEntitlementEntity(Entitlement entitlement) {
        EntitlementEntity entitlementEntity = new EntitlementEntity();
        entitlementEntity.setTrackingUuid(entitlement.getTrackingUuid());
        entitlementEntity.setId(entitlement.getId());
        entitlementEntity.setRev(entitlement.getRev());
        entitlementEntity.setItemId(entitlement.getItemId());
        entitlementEntity.setUserId(entitlement.getUserId());
        entitlementEntity.setIsBanned(entitlement.getIsBanned());
        entitlementEntity.setGrantTime(entitlement.getGrantTime());
        if (entitlement.getExpirationTime() == null) {
            entitlementEntity.setExpirationTime(EntitlementConsts.NEVER_EXPIRE);
        } else {
            entitlementEntity.setExpirationTime(entitlement.getExpirationTime());
        }
        if (entitlement.getUseCount() == null) {
            entitlementEntity.setUseCount(EntitlementConsts.UNCONSUMABLE_USECOUNT);
        } else {
            entitlementEntity.setUseCount(entitlement.getUseCount());
        }
        if (entitlement.getType() == null) {
            entitlementEntity.setType(EntitlementConsts.NO_TYPE);
        } else {
            entitlementEntity.setType(entitlement.getType().toUpperCase());
        }
        entitlementEntity.setFutureExpansion(entitlement.getFutureExpansion());
        return entitlementEntity;
    }


    public List<Entitlement> toEntitlementList(List<EntitlementEntity> entitlementEntities) {
        List<Entitlement> entitlements = new ArrayList<>(entitlementEntities.size());
        for (EntitlementEntity entitlementEntity : entitlementEntities) {
            entitlements.add(toEntitlement(entitlementEntity));
        }
        return entitlements;
    }

    public Results<Entitlement> toEntitlementResults(Results<EntitlementEntity> entityResults) {
        Results<Entitlement> results = new Results<>();
        results.setNext(entityResults.getNext());
        results.setTotal(entityResults.getTotal());
        results.setItems(toEntitlementList(entityResults.getItems()));
        return results;
    }

    private Boolean isActive(EntitlementEntity entitlementEntity) {
        if (entitlementEntity.getIsBanned()) {
            return false;
        }
        Date now = EntitlementContext.current().getNow();
        Date expirationDate = entitlementEntity.getExpirationTime();
        Integer useCount = entitlementEntity.getUseCount();
        if (entitlementEntity.getGrantTime().getTime() - now.getTime() <= 60000L &&
                (expirationDate == null || expirationDate.after(now)) &&
                (useCount == null || useCount > 0)) {
            return true;
        }
        return false;
    }
}
