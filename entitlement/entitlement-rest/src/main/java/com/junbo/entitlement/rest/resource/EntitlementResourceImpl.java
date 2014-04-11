/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementId;
import com.junbo.entitlement.common.def.EntitlementStatusReason;
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
        Entitlement existing = getByTrackingUuid(entitlement.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.addEntitlement(entitlement));
    }

    @Override
    public Promise<Entitlement> updateEntitlement(EntitlementId entitlementId, Entitlement entitlement) {
        Entitlement existing = getByTrackingUuid(entitlement.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.updateEntitlement(entitlementId.getValue(), entitlement));
    }

    @Override
    public Promise<Response> deleteEntitlement(EntitlementId entitlementId) {
        entitlementService.deleteEntitlement(entitlementId.getValue(), EntitlementStatusReason.DELETED);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<Entitlement> transferEntitlement(EntitlementTransfer entitlementTransfer) {
        Entitlement existing = getByTrackingUuid(entitlementTransfer.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.transferEntitlement(entitlementTransfer));
    }

    private Entitlement getByTrackingUuid(UUID trackingUuid) {
        if (trackingUuid != null) {
            Entitlement existingEntitlement
                    = entitlementService.getByTrackingUuid(trackingUuid);
            return existingEntitlement;
        }
        return null;
    }
}
