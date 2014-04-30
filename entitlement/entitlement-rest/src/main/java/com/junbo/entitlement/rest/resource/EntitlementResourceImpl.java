/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.EntitlementTransfer;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;

/**
 * Impl of EntitlementResource.
 */
public class EntitlementResourceImpl implements EntitlementResource {
    @Autowired
    private EntitlementService entitlementService;
    @Autowired
    private UriInfo uriInfo;

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
    public Promise<Results<Entitlement>> searchEntitlements(@BeanParam EntitlementSearchParam searchParam, @BeanParam PageMetadata pageMetadata) {
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, pageMetadata);
        Results<Entitlement> result = new Results<Entitlement>();
        result.setItems(entitlements);

        Link link = new Link();
        if (entitlements.size() <
                (pageMetadata.getCount() == null
                        ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount())) {
            link.setHref(EntitlementConsts.NEXT_END);
        } else {
            link.setHref(buildNextUrl(searchParam, pageMetadata));
        }
        result.setNext(link);
        return Promise.pure(result);
    }

    private String buildNextUrl(
            EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        UriBuilder builder = uriInfo.getBaseUriBuilder().path("entitlements");
        builder.queryParam("userId", IdFormatter.encodeId(searchParam.getUserId()));
        if (!StringUtils.isEmpty(searchParam.getType())) {
            builder = builder.queryParam("type", searchParam.getType());
        }
        if (searchParam.getIsActive() != null) {
            builder = builder.queryParam("isActive", searchParam.getIsActive());
        }
        if (searchParam.getIsBanned() != null) {
            builder = builder.queryParam("isBanned", searchParam.getIsBanned());
        }
        if (!CollectionUtils.isEmpty(searchParam.getDefinitionIds())) {
            for (EntitlementDefinitionId definitionId : searchParam.getDefinitionIds()) {
                builder = builder.queryParam("definitionIds", IdFormatter.encodeId(definitionId));
            }
        }
        builder = CommonUtils.buildPageParams(builder,
                pageMetadata.getStart(), pageMetadata.getCount());
        return builder.toTemplate();
    }

    @Override
    public Promise<Entitlement> transferEntitlement(EntitlementTransfer entitlementTransfer) {
        Entitlement existing = getByTrackingUuid(entitlementTransfer.getTargetUserId(),
                entitlementTransfer.getTrackingUuid());
        return Promise.pure(existing != null ? existing :
                entitlementService.transferEntitlement(entitlementTransfer));
    }

//    @Override
    public Promise<Boolean> isDeveloper(UserId userId) {
        return Promise.pure(entitlementService.isDeveloper(userId.getValue()));
    }

//    @Override
    public Promise<Entitlement> grantDeveloperEntitlement(UserId userId) {
        return Promise.pure(entitlementService.grantDeveloperEntitlement(userId.getValue()));
    }

//    @Override
    public Promise<Boolean> canDownload(UserId userId, ItemId itemId) {
        return Promise.pure(entitlementService.canDownload(userId.getValue(), itemId.getValue()));
    }

//    @Override
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
