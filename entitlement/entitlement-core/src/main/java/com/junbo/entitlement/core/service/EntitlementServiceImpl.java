/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.common.lib.CloneUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

/**
 * Service of Entitlement.
 */
public class EntitlementServiceImpl extends BaseService implements EntitlementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementService.class);
    @Autowired
    private EntitlementRepository entitlementRepository;

    @Override
    @Transactional
    public Entitlement getEntitlement(String entitlementId) {
        Entitlement entitlement = entitlementRepository.get(entitlementId);
        if (entitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    entitlementId).exception();
        }
        return entitlement;
    }

    @Override
    @Transactional
    public Entitlement addEntitlement(Entitlement entitlement) {
        fillCreate(entitlement);
        validateCreate(entitlement);
        return merge(entitlement);
    }

    private Entitlement merge(Entitlement entitlement) {
        ItemRevision item = getItem(entitlement.getItemId());
        EntitlementDef def = filter(item.getEntitlementDefs(), entitlement.getType());
        if (!def.getConsumable()) {
            return entitlementRepository.insert(entitlement);
        }

        Entitlement existing = entitlementRepository.get(entitlement.getUserId(), entitlement.getItemId(), entitlement.getType());
        if (existing == null) {
            return entitlementRepository.insert(entitlement);
        }

        existing.setIsBanned(false);
        existing.setUseCount(existing.getUseCount() + entitlement.getUseCount());
        return entitlementRepository.update(existing);
    }

    @Override
    @Transactional
    public Entitlement updateEntitlement(String entitlementId, Entitlement entitlement) {
        validateUpdateId(entitlementId, entitlement);
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementId).exception();
        }
        fillUpdate(entitlement, existingEntitlement);
        validateUpdate(entitlement, existingEntitlement);
        return entitlementRepository.update(existingEntitlement);
    }

    @Override
    @Transactional
    public void deleteEntitlement(String entitlementId) {
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    entitlementId).exception();
        }
        checkUser(existingEntitlement.getUserId());
        entitlementRepository.delete(entitlementId);
    }

    @Override
    @Transactional
    public Results<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                                  PageMetadata pageMetadata) {
        validateNotNull(entitlementSearchParam.getUserId(), "userId");
        checkUser(entitlementSearchParam.getUserId().getValue());
        fillClient(entitlementSearchParam);
        fillHostItemId(entitlementSearchParam);
        checkSearchDateFormat(entitlementSearchParam);
        checkIsActiveAndIsBanned(entitlementSearchParam);
        Results<Entitlement> results = entitlementRepository.getBySearchParam(
                entitlementSearchParam, pageMetadata);
        return results;
    }

    private void fillHostItemId(EntitlementSearchParam entitlementSearchParam) {
        if (entitlementSearchParam.getHostItemId() == null) {
            return;
        }
        Set<String> itemIds = itemFacade.getItemIdsByHostItemId(entitlementSearchParam.getHostItemId().getValue());
        if (CollectionUtils.isEmpty(itemIds)) {
            throw AppErrors.INSTANCE.fieldNotCorrect("hostItemId",
                    "there is no item with hostItemId [" + entitlementSearchParam.getHostItemId() + "]").exception();
        }
        for (String itemId : itemIds) {
            entitlementSearchParam.getItemIds().add(new ItemId(itemId));
        }
    }

    private void checkIsActiveAndIsBanned(EntitlementSearchParam entitlementSearchParam) {
        if (Boolean.TRUE.equals(entitlementSearchParam.getIsBanned()) &&
                Boolean.TRUE.equals(entitlementSearchParam.getIsActive())) {
            throw AppErrors.INSTANCE.common("isActive and isSuspended can not be set to true at same time").exception();
        }
    }

    private void fillClient(EntitlementSearchParam entitlementSearchParam) {
        //TODO: get item by clientId and then set the itemIds to entitlementSearchParam.
    }

    private void checkSearchDateFormat(EntitlementSearchParam entitlementSearchParam) {
        checkDateFormat(entitlementSearchParam.getStartGrantTime());
        checkDateFormat(entitlementSearchParam.getEndGrantTime());
        checkDateFormat(entitlementSearchParam.getStartExpirationTime());
        checkDateFormat(entitlementSearchParam.getEndExpirationTime());
        checkDateFormat(entitlementSearchParam.getLastModifiedTime());
    }

    @Override
    @Transactional
    public Entitlement transferEntitlement(EntitlementTransfer entitlementTransfer) {
        Entitlement existingEntitlement = getEntitlement(entitlementTransfer.getEntitlementId());
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    entitlementTransfer.getEntitlementId()).exception();
        }
        validateTransfer(entitlementTransfer, existingEntitlement);

        Entitlement newEntitlement = CloneUtils.clone(existingEntitlement);
        deleteEntitlement(entitlementTransfer.getEntitlementId());
        LOGGER.info("Entitlement [{}] is deleted for transferring.", existingEntitlement.getId());
        newEntitlement.setTrackingUuid(entitlementTransfer.getTrackingUuid());
        newEntitlement.setId(null);
        newEntitlement.setUserId(entitlementTransfer.getTargetUserId());
        return entitlementRepository.insert(newEntitlement);
    }

    @Override
    @Transactional
    public Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(shardMasterId, trackingUuid);
    }
}
