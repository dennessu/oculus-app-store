/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.entitlement.common.def.EntitlementStatusReason;
import com.junbo.entitlement.common.lib.CloneUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.repository.EntitlementDefinitionRepository;
import com.junbo.entitlement.db.repository.EntitlementRepository;
import com.junbo.entitlement.spec.error.AppErrors;
import com.junbo.entitlement.spec.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private EntitlementDefinitionRepository entitlementDefinitionRepository;

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
        checkUser(entitlement.getUserId());
        checkOffer(entitlement.getOfferId());
        checkDeveloper(entitlement.getDeveloperId());

        if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS.contains(
                EntitlementStatus.valueOf(entitlement.getStatus()))) {
            LOGGER.error("Can not created {} entitlement.", entitlement.getStatus());
            throw AppErrors.INSTANCE.fieldNotCorrect("status",
                    "status can not be DELETED or BANNED when created").exception();
        }

        checkEntitlementDefinition(entitlement.getEntitlementDefinitionId());

        validateGrantTimeBeforeExpirationTime(entitlement);

        if (entitlement.getType() == null) {
            entitlement.setType(EntitlementType.DEFAULT.toString());
        }

        if (entitlement.getManagedLifecycle() == null) {
            LOGGER.warn("managedLifecycle not found, set true as default.");
            entitlement.setManagedLifecycle(true);
        }

        if (entitlement.getConsumable() == null || !entitlement.getConsumable()) {
            LOGGER.warn("consumable not found, set false and set useCount to 0 as default.");
            entitlement.setConsumable(false);
            entitlement.setUseCount(0);
        }

        //if managedLifecycle is true, try to merge the added entitlement into existing entitlement
        //not deal with developer entitlement as developer entitlement is bounded to userId only
        if (Boolean.TRUE.equals(entitlement.getManagedLifecycle()) &&
                !entitlement.getType().equalsIgnoreCase(EntitlementType.DEVELOPER.toString())) {
            Entitlement existingEntitlement = entitlementRepository.getExistingManagedEntitlement(
                    entitlement.getUserId(), entitlement.getEntitlementDefinitionId());
            if (existingEntitlement != null) {
                LOGGER.info("Merge added entitlement into existing entitlement [{}].",
                        existingEntitlement.getEntitlementId());
                if (entitlement.getExpirationTime() != null) {
                    existingEntitlement.setExpirationTime(new Date(existingEntitlement.getExpirationTime().getTime()
                            + entitlement.getExpirationTime().getTime()
                            - entitlement.getGrantTime().getTime()));
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



    @Override
    @Transactional
    public Entitlement updateEntitlement(Long entitlementId, Entitlement entitlement) {
        if (entitlement.getEntitlementId() == null) {
            throw AppErrors.INSTANCE.missingField("id").exception();
        }
        if (!entitlementId.equals(entitlement.getEntitlementId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("id", entitlement.getEntitlementId(),
                    entitlementId).exception();
        }

        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);

        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementId).exception();
        }

        checkUser(existingEntitlement.getUserId());

        if (!existingEntitlement.getUserId().equals(entitlement.getUserId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("userId", entitlement.getUserId(),
                    existingEntitlement.getUserId()).exception();
        }
        if (!existingEntitlement.getOfferId().equals(entitlement.getOfferId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("offerId", entitlement.getOfferId(),
                    existingEntitlement.getOfferId()).exception();
        }
        if (!existingEntitlement.getEntitlementDefinitionId().equals(entitlement.getEntitlementDefinitionId())) {
            throw AppErrors.INSTANCE.fieldNotMatch("EntitlementDefinitionId",
                    entitlement.getEntitlementDefinitionId(),
                    existingEntitlement.getEntitlementDefinitionId()).exception();
        }
        if (!existingEntitlement.getType().equals(entitlement.getType())) {
            throw AppErrors.INSTANCE.fieldNotMatch("type",
                    entitlement.getType(),
                    existingEntitlement.getType()).exception();
        }
        if (!existingEntitlement.getGroup().equals(entitlement.getGroup())) {
            throw AppErrors.INSTANCE.fieldNotMatch("group",
                    entitlement.getGroup(),
                    existingEntitlement.getGroup()).exception();
        }
        if (!existingEntitlement.getTag().equals(entitlement.getTag())) {
            throw AppErrors.INSTANCE.fieldNotMatch("tag",
                    entitlement.getTag(),
                    existingEntitlement.getTag()).exception();
        }

        if (existingEntitlement.getGrantTime().compareTo(entitlement.getGrantTime()) != 0) {
            throw AppErrors.INSTANCE.fieldNotMatch("grantTime",
                    entitlement.getGrantTime(),
                    existingEntitlement.getGrantTime()).exception();
        }

        existingEntitlement.setExpirationTime(entitlement.getExpirationTime());
        validateGrantTimeBeforeExpirationTime(existingEntitlement);

        existingEntitlement.setStatus(entitlement.getStatus());
        existingEntitlement.setStatusReason(entitlement.getStatusReason());

        if (entitlement.getManagedLifecycle() == null) {
            existingEntitlement.setManagedLifecycle(true);
        } else {
            existingEntitlement.setManagedLifecycle(entitlement.getManagedLifecycle());
        }

        if (entitlement.getConsumable() == null || !entitlement.getConsumable()) {
            existingEntitlement.setConsumable(false);
            existingEntitlement.setUseCount(0);
        } else {
            existingEntitlement.setConsumable(entitlement.getConsumable());
            existingEntitlement.setUseCount(entitlement.getUseCount());
        }

        if (EntitlementStatus.LIFECYCLE_NOT_MANAGED_STATUS.contains(entitlement.getStatus())) {
            LOGGER.info("Delete or ban entitlement [{}], set managedLifecycle" +
                    " and consumable to false and set useCount to 0.", entitlementId);
            existingEntitlement.setManagedLifecycle(false);
            existingEntitlement.setConsumable(false);
            existingEntitlement.setUseCount(0);
        }

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
        if (entitlementSearchParam.getUserId() == null) {
            throw AppErrors.INSTANCE.missingField("userId").exception();
        }
        if (entitlementSearchParam.getDeveloperId() == null) {
            throw AppErrors.INSTANCE.missingField("developerId").exception();
        }
        checkUser(entitlementSearchParam.getUserId().getValue());
        checkDeveloper(entitlementSearchParam.getDeveloperId().getValue());
        List<Entitlement> entitlementEntities = entitlementRepository.getBySearchParam(
                entitlementSearchParam, pageMetadata);
        return entitlementEntities;
    }

    @Override
    @Transactional
    public Entitlement transferEntitlement(EntitlementTransfer entitlementTransfer) {
        if (entitlementTransfer.getUserId() == null) {
            throw AppErrors.INSTANCE.missingField("userId").exception();
        }
        if (entitlementTransfer.getEntitlementId() == null) {
            throw AppErrors.INSTANCE.missingField("entitlementId").exception();
        }
        if (entitlementTransfer.getTargetUserId() == null) {
            throw AppErrors.INSTANCE.missingField("targetUserId").exception();
        }

        checkUser(entitlementTransfer.getUserId());

        Entitlement existingEntitlement = getEntitlement(entitlementTransfer.getEntitlementId());

        if (EntitlementStatus.NOT_TRANSFERABLE.contains(existingEntitlement.getStatus())) {
            LOGGER.error("Entitlement [{}] can not be transferred.", existingEntitlement.getEntitlementId());
            throw AppErrors.INSTANCE.notTransferable(existingEntitlement.getEntitlementId(),
                    "Entitlement with status " +
                            existingEntitlement.getStatus() +
                            " can not be transferred")
                    .exception();
        }

        Entitlement newEntitlement = CloneUtils.clone(existingEntitlement);
        deleteEntitlement(entitlementTransfer.getEntitlementId(),
                EntitlementStatusReason.TRANSFERRED);
        LOGGER.info("Entitlement [{}] is deleted for transferring.", existingEntitlement.getEntitlementId());
        newEntitlement.setTrackingUuid(entitlementTransfer.getTrackingUuid());
        newEntitlement.setEntitlementId(null);
        newEntitlement.setUserId(entitlementTransfer.getTargetUserId());
        return addEntitlement(newEntitlement);
    }

    private void validateGrantTimeBeforeExpirationTime(Entitlement entitlement) {
        if (entitlement.getExpirationTime() != null) {
            if (entitlement.getGrantTime().after(entitlement.getExpirationTime())) {
                throw AppErrors.INSTANCE.expirationTimeBeforeGrantTime().exception();
            }
        }
    }

    @Override
    @Transactional
    public Entitlement getByTrackingUuid(UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(trackingUuid);
    }

    private void checkEntitlementDefinition(Long entitlementDefinitionId) {

    }
}
