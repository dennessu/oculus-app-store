/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.model.RevokeRequest;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Map;

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
        checkBodyNotNull(entitlement);
        return Promise.pure(entitlementService.addEntitlement(entitlement));
    }

    @Override
    public Promise<Map<Long, List<Entitlement>>> postEntitlements(Map<Long, List<Entitlement>> entitlements) {
        checkBodyNotNull(entitlements);
        return Promise.pure(entitlementService.addEntitlements(entitlements));
    }

    @Override
    public Promise<Entitlement> updateEntitlement(EntitlementId entitlementId, Entitlement entitlement) {
        checkBodyNotNull(entitlement);
        return Promise.pure(entitlementService.updateEntitlement(entitlementId.getValue(), entitlement));
    }

    @Override
    public Promise<Response> deleteEntitlement(EntitlementId entitlementId) {
        entitlementService.deleteEntitlement(entitlementId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    @Override
    public Promise<Results<Entitlement>> searchEntitlements(EntitlementSearchParam searchParam, PageMetadata pageMetadata) {
        Results<Entitlement> result = entitlementService.searchEntitlement(searchParam, pageMetadata);

        Link link = new Link();
        link.setHref(buildNextUrl(searchParam, pageMetadata, result.getNext()));
        result.setNext(link);
        return Promise.pure(result);
    }

    @Override
    public Promise<Response> revokeEntitlement(RevokeRequest revokeRequest) {
        entitlementService.revokeEntitlement(revokeRequest);
        return Promise.pure(Response.status(200).build());
    }

    private String buildNextUrl(
            EntitlementSearchParam searchParam, PageMetadata pageMetadata, Link next) {
        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("entitlements");
        builder.queryParam("userId", IdFormatter.encodeId(searchParam.getUserId()));
        if (!StringUtils.isEmpty(searchParam.getType())) {
            builder = builder.queryParam("type", searchParam.getType());
        }
        if (searchParam.getIsActive() != null) {
            builder = builder.queryParam("isActive", searchParam.getIsActive());
        }
        if (searchParam.getIsBanned() != null) {
            builder = builder.queryParam("isSuspended", searchParam.getIsBanned());
        }
        if (searchParam.getHostItemId() != null) {
            builder = builder.queryParam("hostItemId", IdFormatter.encodeId(searchParam.getHostItemId()));
        }
        if (!CollectionUtils.isEmpty(searchParam.getItemIds())) {
            for (ItemId itemId : searchParam.getItemIds()) {
                builder = builder.queryParam("itemIds", IdFormatter.encodeId(itemId));
            }
        }
        builder = CommonUtils.buildPageParams(builder,
                pageMetadata.getStart(), pageMetadata.getCount(), next == null ? null : next.getHref());
        return builder.toTemplate();
    }

    private void checkBodyNotNull(Object value) {
        if (value == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception();
        }
    }
}
