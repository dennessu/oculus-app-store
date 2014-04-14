/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.mapper;

import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.EntitlementEntity;
import com.junbo.entitlement.spec.def.EntitlementStatus;
import com.junbo.entitlement.spec.def.EntitlementType;
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
        entitlement.setType(entitlementEntity.getType().toString());
        entitlement.setGroup(entitlementEntity.getGroup());
        entitlement.setTag(entitlementEntity.getTag());
        entitlement.setInAppContext(entitlementEntity.getInAppContext());
        entitlement.setEntitlementDefinitionId(entitlementEntity.getEntitlementDefinitionId());
        entitlement.setStatus(getStatus(entitlementEntity).toString());
        entitlement.setStatusReason(entitlementEntity.getStatusReason());
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
        entitlementEntity.setInAppContext(entitlement.getInAppContext());
        entitlementEntity.setType(EntitlementType.valueOf(entitlement.getType().toUpperCase()));
        entitlementEntity.setGroup(entitlement.getGroup());
        entitlementEntity.setTag(entitlement.getTag());
        entitlementEntity.setUserId(entitlement.getUserId());
        entitlementEntity.setStatus(getStatus(entitlement));
        entitlementEntity.setStatusReason(entitlement.getStatusReason());
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

    private EntitlementStatus getStatus(EntitlementEntity entitlementEntity) {
        if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS
                .contains(entitlementEntity.getStatus())) {
            return entitlementEntity.getStatus();
        } else {
            Date now = EntitlementContext.current().getNow();
            if (entitlementEntity.getUseCount() != null
                    && entitlementEntity.getUseCount() < 1) {
                return EntitlementStatus.DISABLED;
            } else if (now.before(entitlementEntity.getGrantTime())) {
                return EntitlementStatus.PENDING;
            } else if (entitlementEntity.getExpirationTime() == null
                    || now.before(entitlementEntity.getExpirationTime())) {
                return EntitlementStatus.ACTIVE;
            } else {
                return EntitlementStatus.DISABLED;
            }
        }
    }

    private EntitlementStatus getStatus(Entitlement entitlement) {
        if (entitlement.getStatus() != null) {
            EntitlementStatus status = EntitlementStatus.valueOf(entitlement.getStatus().toUpperCase());
            if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS
                    .contains(status)) {
                return status;
            }
        }
        return EntitlementStatus.MANAGED;
    }
}
