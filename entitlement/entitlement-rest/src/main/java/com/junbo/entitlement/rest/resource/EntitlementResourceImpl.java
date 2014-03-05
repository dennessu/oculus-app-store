/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

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
    public Promise<Entitlement> getEntitlement(Long entitlementId) {
        Entitlement entitlement = entitlementService.getEntitlement(entitlementId);
        return Promise.pure(entitlement);
    }

    @Override
    public Promise<ResultList<Entitlement>> getEntitlements(Long userId,
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
    public Promise<Entitlement> updateEntitlement(Long entitlementId, Entitlement entitlement) {
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
        return Promise.pure(entitlementService.updateEntitlement(entitlementId, entitlement));
    }

    @Override
    public Promise<Response> deleteEntitlement(Long entitlementId) {
        entitlementService.deleteEntitlement(entitlementId, EntitlementStatusReason.DELETED);
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<ResultList<Entitlement>> searchEntitlements(
            EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        List<Entitlement> entitlements = entitlementService.searchEntitlement(searchParam, pageMetadata);
        ResultList<Entitlement> result = new ResultList<Entitlement>();
        result.setCriteria(entitlements);
        if (entitlements.size() < (pageMetadata.getCount() == null
                ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount())) {
            result.setNext(EntitlementConsts.NEXT_END);
        } else {
            result.setNext(CommonUtils.buildPageParams(
                    uriInfo.getBaseUriBuilder().path("entitlements").path("search"),
                    pageMetadata.getStart(), pageMetadata.getCount()).toTemplate());
        }
        return Promise.pure(result);
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
                .path(searchParam.getUserId().toString())
                .path("entitlements")
                .queryParam("developerId", searchParam.getDeveloperId());
        if (!StringUtils.isEmpty(searchParam.getType())) {
            builder = builder.queryParam("type", searchParam.getType());
        }
        if (!StringUtils.isEmpty(searchParam.getStatus())) {
            builder = builder.queryParam("status", searchParam.getStatus());
        }
        if (!CollectionUtils.isEmpty(searchParam.getOfferIds())) {
            for (Long offerId : searchParam.getOfferIds()) {
                builder = builder.queryParam("offerIds", offerId);
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
            for (Long definitionId : searchParam.getDefinitionIds()) {
                builder = builder.queryParam("definitionIds", definitionId);
            }
        }
        builder = CommonUtils.buildPageParams(builder,
                pageMetadata.getStart(), pageMetadata.getCount());
        return builder.toTemplate();
    }
}
