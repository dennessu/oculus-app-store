/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.entitlement.common.def.EntitlementStatusReason;
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
import org.springframework.util.StringUtils;

import java.util.Date;
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
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementId).exception();
        }
        return entitlement;
    }

    @Override
    @Transactional
    public Entitlement addEntitlement(Entitlement entitlement) {
        fillCreate(entitlement);
        validateCreate(entitlement);
        //if managedLifecycle is true, try to merge the added entitlement into existing entitlement
        return merge(entitlement);
    }

    @Override
    @Transactional
    public Entitlement updateEntitlement(Long entitlementId, Entitlement entitlement) {
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
    public void deleteEntitlement(Long entitlementId, String reason) {
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementId).exception();
        }
        checkUser(existingEntitlement.getUserId());
        entitlementRepository.delete(existingEntitlement, reason);
    }

    @Override
    @Transactional
    public List<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                               PageMetadata pageMetadata) {
        validateNotNull(entitlementSearchParam.getUserId(), "user");
        if (StringUtils.isEmpty(entitlementSearchParam.getOwnerId())) {
            //TODO: check if clientId is admin
        } else {
            checkOwner(entitlementSearchParam.getOwnerId());
        }
        checkUser(entitlementSearchParam.getUserId().getValue());
        List<Entitlement> entitlementEntities = entitlementRepository.getBySearchParam(
                entitlementSearchParam, pageMetadata);
        return entitlementEntities;
    }

    @Override
    @Transactional
    public Entitlement transferEntitlement(EntitlementTransfer entitlementTransfer) {
        Entitlement existingEntitlement = getEntitlement(entitlementTransfer.getEntitlementId());
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementTransfer.getEntitlementId()).exception();
        }
        validateTransfer(entitlementTransfer, existingEntitlement);

        Entitlement newEntitlement = CloneUtils.clone(existingEntitlement);
        deleteEntitlement(entitlementTransfer.getEntitlementId(),
                EntitlementStatusReason.TRANSFERRED);
        LOGGER.info("Entitlement [{}] is deleted for transferring.", existingEntitlement.getEntitlementId());
        newEntitlement.setTrackingUuid(entitlementTransfer.getTrackingUuid());
        newEntitlement.setEntitlementId(null);
        newEntitlement.setUserId(entitlementTransfer.getTargetUserId());
        return addEntitlement(newEntitlement);
    }

    @Override
    @Transactional
    public Entitlement getByTrackingUuid(UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(trackingUuid);
    }

    private Entitlement merge(Entitlement entitlement) {
        if (Boolean.TRUE.equals(entitlement.getManagedLifecycle())) {
            Entitlement existingEntitlement;
            if (entitlement.getEntitlementDefinitionId() != null) {
                existingEntitlement = entitlementRepository.getExistingManagedEntitlement(
                        entitlement.getUserId(), entitlement.getEntitlementDefinitionId());
            } else {
                existingEntitlement = entitlementRepository.getExistingManagedEntitlement(
                        entitlement.getUserId(), entitlement.getType(),
                        entitlement.getOwnerId(), entitlement.getGroup(),
                        entitlement.getTag(), entitlement.getConsumable());
            }
            if (existingEntitlement != null) {
                LOGGER.info("Merge added entitlement into existing entitlement [{}].",
                        existingEntitlement.getEntitlementId());
                if (entitlement.getExpirationTime() != null) {
                    if (existingEntitlement.getExpirationTime() != null) {
                        existingEntitlement.setExpirationTime(new Date(existingEntitlement.getExpirationTime().getTime()
                                + entitlement.getExpirationTime().getTime()
                                - entitlement.getGrantTime().getTime()));
                    }
                } else {
                    existingEntitlement.setExpirationTime(null);
                }
                if (entitlement.getConsumable()) {
                    existingEntitlement.setUseCount(existingEntitlement.getUseCount() + entitlement.getUseCount());
                }

                return entitlementRepository.update(existingEntitlement);
            }
        }
        return entitlementRepository.insert(entitlement);
    }
}
