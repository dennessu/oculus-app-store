/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.lib.CloneUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
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

import java.util.Collections;
import java.util.List;
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
    public Entitlement getEntitlement(Long entitlementId) {
        Entitlement entitlement = entitlementRepository.get(entitlementId);
        if (entitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    formatId(entitlementId)).exception();
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
        EntitlementDefinition def = getDef(entitlement.getEntitlementDefinitionId());
        if (!def.getConsumable()) {
            return entitlementRepository.insert(entitlement);
        }

        Entitlement existing = entitlementRepository.get(entitlement.getUserId(), entitlement.getEntitlementDefinitionId());
        if (existing == null) {
            return entitlementRepository.insert(entitlement);
        }

        existing.setIsBanned(false);
        existing.setUseCount(existing.getUseCount() + entitlement.getUseCount());
        return entitlementRepository.update(existing);
    }

    @Override
    @Transactional
    public Entitlement updateEntitlement(Long entitlementId, Entitlement entitlement) {
        validateUpdateId(entitlementId, entitlement);
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", formatId(entitlementId)).exception();
        }
        fillUpdate(entitlement, existingEntitlement);
        validateUpdate(entitlement, existingEntitlement);
        return entitlementRepository.update(existingEntitlement);
    }

    @Override
    @Transactional
    public void deleteEntitlement(Long entitlementId) {
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement",
                    formatId(entitlementId)).exception();
        }
        checkUser(existingEntitlement.getUserId());
        entitlementRepository.delete(entitlementId);
    }

    @Override
    @Transactional
    public List<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                               PageMetadata pageMetadata) {
        validateNotNull(entitlementSearchParam.getUserId(), "userId");
        checkUser(entitlementSearchParam.getUserId().getValue());
        fillClient(entitlementSearchParam);
        checkSearchDateFormat(entitlementSearchParam);
        List<Entitlement> entitlementEntities = entitlementRepository.getBySearchParam(
                entitlementSearchParam, pageMetadata);
        return entitlementEntities;
    }

    private void fillClient(EntitlementSearchParam entitlementSearchParam) {
        //TODO: get entitlementDef by clientId and then set the defIds to entitlementSearchParam.
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
                    formatId(entitlementTransfer.getEntitlementId())).exception();
        }
        validateTransfer(entitlementTransfer, existingEntitlement);

        Entitlement newEntitlement = CloneUtils.clone(existingEntitlement);
        deleteEntitlement(entitlementTransfer.getEntitlementId());
        LOGGER.info("Entitlement [{}] is deleted for transferring.", existingEntitlement.getEntitlementId());
        newEntitlement.setTrackingUuid(entitlementTransfer.getTrackingUuid());
        newEntitlement.setEntitlementId(null);
        newEntitlement.setUserId(entitlementTransfer.getTargetUserId());
        return entitlementRepository.insert(newEntitlement);
    }

    @Override
    @Transactional
    public Entitlement grantDeveloperEntitlement(Long userId) {
        Entitlement entitlement = new Entitlement();
        entitlement.setIsBanned(false);
        entitlement.setUserId(userId);
        entitlement.setGrantTime(EntitlementContext.current().getNow());
        entitlement.setEntitlementDefinitionId(getDevDef().getEntitlementDefId());
        entitlement.setType("DEVELOPER");
        return entitlementRepository.insert(entitlement);
    }

    @Override
    @Transactional
    public Boolean isDeveloper(Long userId) {
        EntitlementSearchParam searchParam = new EntitlementSearchParam
                .Builder(new UserId(userId))
                .definitionIds(
                        Collections.singleton(
                                new EntitlementDefinitionId(getDevDef().getEntitlementDefId()))).build();
        List<Entitlement> result = entitlementRepository.getBySearchParam(searchParam, null);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    @Transactional
    public Boolean canDownload(Long userId, Long itemId) {
        EntitlementSearchParam searchParam = new EntitlementSearchParam
                .Builder(new UserId(userId))
                .definitionIds(
                        Collections.singleton(
                                new EntitlementDefinitionId(getDownloadDef(itemId).getEntitlementDefId()))).build();
        List<Entitlement> result = entitlementRepository.getBySearchParam(searchParam, null);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    @Transactional
    public Boolean canAccess(Long userId, Long itemId) {
        EntitlementSearchParam searchParam = new EntitlementSearchParam
                .Builder(new UserId(userId))
                .definitionIds(
                        Collections.singleton(
                                new EntitlementDefinitionId(getAccessDef(itemId).getEntitlementDefId()))).build();
        List<Entitlement> result = entitlementRepository.getBySearchParam(searchParam, null);
        return !CollectionUtils.isEmpty(result);
    }

    @Override
    @Transactional
    public Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(shardMasterId, trackingUuid);
    }
}
