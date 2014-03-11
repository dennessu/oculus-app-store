/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.def.EntitlementStatusReason;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.*;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    public Promise<ResultList<Entitlement>> getEntitlements(UserId userId,
                                                            EntitlementSearchParam searchParam,
                                                            PageMetadata pageMetadata) {
        searchParam.setUserId(userId);
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, pageMetadata);
        ResultList<Entitlement> result = new ResultList<Entitlement>();
        result.setCriteria(entitlements);
        if (entitlements.size() <
                (pageMetadata.getCount() == null
                        ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount())) {
            result.setNext(EntitlementConsts.NEXT_END);
        } else {
            result.setNext(buildNextUrl(searchParam, pageMetadata));
        }
        return Promise.pure(result);
    }

    @Override
    public Promise<Entitlement> postEntitlement(Entitlement entitlement) {
        UUID trackingUuid = entitlement.getTrackingUuid();
        if (trackingUuid != null) {
            Entitlement existingEntitlement
                    = entitlementService.getByTrackingUuid(trackingUuid);
            if (existingEntitlement != null) {
                return Promise.pure(existingEntitlement);
            }
        }

        if (entitlement.getPeriod() != null) {
            entitlement.setExpirationTime(new Date(entitlement.getGrantTime().getTime()
                    + TimeUnit.SECONDS.toMillis(entitlement.getPeriod())));
        }
        return Promise.pure(entitlementService.addEntitlement(entitlement));
    }

    @Override
    public Promise<Entitlement> updateEntitlement(EntitlementId entitlementId, Entitlement entitlement) {
        UUID trackingUuid = entitlement.getTrackingUuid();
        if (trackingUuid != null) {
            Entitlement existingEntitlement
                    = entitlementService.getByTrackingUuid(trackingUuid);
            if (existingEntitlement != null) {
                return Promise.pure(existingEntitlement);
            }
        }

        if (entitlement.getPeriod() != null) {
            entitlement.setExpirationTime(new Date(entitlement.getGrantTime().getTime()
                    + TimeUnit.SECONDS.toMillis(entitlement.getPeriod())));
        }
        return Promise.pure(entitlementService.updateEntitlement(entitlementId.getValue(), entitlement));
    }

    @Override
    public Promise<Response> deleteEntitlement(EntitlementId entitlementId) {
        entitlementService.deleteEntitlement(entitlementId.getValue(), EntitlementStatusReason.DELETED);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<Entitlement> transferEntitlement(EntitlementTransfer entitlementTransfer) {
        UUID trackingUuid = entitlementTransfer.getTrackingUuid();
        if (trackingUuid != null) {
            Entitlement existingEntitlement = entitlementService.getByTrackingUuid(trackingUuid);
            if (existingEntitlement != null) {
                return Promise.pure(existingEntitlement);
            }
        }
        return Promise.pure(entitlementService.transferEntitlement(entitlementTransfer));
    }

    private String buildNextUrl(
            EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        UriBuilder builder = uriInfo.getBaseUriBuilder().path("users")
                .path(IdFormatter.encodeId(searchParam.getUserId()))
                .path("entitlements")
                .queryParam("developerId", IdFormatter.encodeId(searchParam.getDeveloperId()));
        if (!StringUtils.isEmpty(searchParam.getType())) {
            builder = builder.queryParam("type", searchParam.getType());
        }
        if (!StringUtils.isEmpty(searchParam.getStatus())) {
            builder = builder.queryParam("status", searchParam.getStatus());
        }
        if (!CollectionUtils.isEmpty(searchParam.getOfferIds())) {
            for (OfferId offerId : searchParam.getOfferIds()) {
                builder = builder.queryParam("offerIds", IdFormatter.encodeId(offerId));
            }
        }
        if (!CollectionUtils.isEmpty(searchParam.getGroups())) {
            for (String group : searchParam.getGroups()) {
                builder = builder.queryParam("groups", group);
            }
        }
        if (!CollectionUtils.isEmpty(searchParam.getTags())) {
            for (String tag : searchParam.getTags()) {
                builder = builder.queryParam("tags", tag);
            }
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
}
