/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Impl of EntitlementResource.
 */
public class EntitlementResourceImpl implements EntitlementResource {
    @Autowired
    private EntitlementService entitlementService;

    @Override
    public Promise<Entitlement> getEntitlement(EntitlementId entitlementId) {
        Entitlement entitlement = entitlementService.getEntitlement(entitlementId.getValue());
        return Promise.pure(entitlement);
    }

    @Override
    public Promise<Entitlement> postEntitlement(Entitlement entitlement) {
        Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.addEntitlement(entitlement));
    }

    @Override
    public Promise<Entitlement> updateEntitlement(EntitlementId entitlementId, Entitlement entitlement) {
        Entitlement existing = getByTrackingUuid(entitlement.getUserId(), entitlement.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.updateEntitlement(entitlementId.getValue(), entitlement));
    }

    @Override
    public Promise<Response> deleteEntitlement(EntitlementId entitlementId) {
        entitlementService.deleteEntitlement(entitlementId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<Entitlement> transferEntitlement(EntitlementTransfer entitlementTransfer) {
        Entitlement existing = getByTrackingUuid(entitlementTransfer.getTargetUserId(),
                entitlementTransfer.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.transferEntitlement(entitlementTransfer));
    }

    @Override
    public Promise<Boolean> isDeveloper(UserId userId) {
        return Promise.pure(entitlementService.isDeveloper(userId.getValue()));
    }

    @Override
    public Promise<Entitlement> grantDeveloperEntitlement(UserId userId) {
        return Promise.pure(entitlementService.grantDeveloperEntitlement(userId.getValue()));
    }

    @Override
    public Promise<Boolean> canDownload(UserId userId, ItemId itemId) {
        return Promise.pure(entitlementService.canDownload(userId.getValue(), itemId.getValue()));
    }

    @Override
    public Promise<Boolean> canAccess(UserId userId, ItemId itemId) {
        return Promise.pure(entitlementService.canAccess(userId.getValue(), itemId.getValue()));
    }

    private Entitlement getByTrackingUuid(Long shardMasterId, UUID trackingUuid) {
        if (trackingUuid != null) {
            Entitlement existingEntitlement
                    = entitlementService.getByTrackingUuid(shardMasterId, trackingUuid);
            return existingEntitlement;
        }
        return null;
    }
}
