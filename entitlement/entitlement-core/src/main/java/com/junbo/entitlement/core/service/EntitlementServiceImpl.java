/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

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
        return entitlementRepository.insert(entitlement);
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
    public void deleteEntitlement(Long entitlementId) {
        Entitlement existingEntitlement = entitlementRepository.get(entitlementId);
        if (existingEntitlement == null) {
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementId).exception();
        }
        checkUser(existingEntitlement.getUserId());
        entitlementRepository.delete(existingEntitlement);
    }

    @Override
    @Transactional
    public List<Entitlement> searchEntitlement(EntitlementSearchParam entitlementSearchParam,
                                               PageMetadata pageMetadata) {
        validateNotNull(entitlementSearchParam.getUserId(), "user");
        checkUser(entitlementSearchParam.getUserId().getValue());
        checkInAppContext(null); //TODO:
        checkSearchDateFormat(entitlementSearchParam);
        List<Entitlement> entitlementEntities = entitlementRepository.getBySearchParam(
                entitlementSearchParam, pageMetadata);
        return entitlementEntities;
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
            throw AppErrors.INSTANCE.notFound("entitlement", entitlementTransfer.getEntitlementId()).exception();
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
    public Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        return entitlementRepository.getByTrackingUuid(shardMasterId, trackingUuid);
    }
}
