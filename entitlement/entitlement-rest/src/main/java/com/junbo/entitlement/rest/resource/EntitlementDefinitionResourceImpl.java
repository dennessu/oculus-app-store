/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.core.EntitlementDefinitionService;
import com.junbo.entitlement.spec.model.EntitlementDefinition;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.model.ResultList;
import com.junbo.entitlement.spec.resource.EntitlementDefinitionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.UUID;

/**
 * Impl of EntitlementDefinitionResource.
 */
public class EntitlementDefinitionResourceImpl implements EntitlementDefinitionResource {
    @Autowired
    private EntitlementDefinitionService entitlementDefinitionService;
    @Autowired
    private UriInfo uriInfo;

    @Override
    public Promise<EntitlementDefinition> getEntitlementDefinition(EntitlementDefinitionId entitlementDefinitionId) {
        EntitlementDefinition entitlementDefinition =
                entitlementDefinitionService.getEntitlementDefinition(entitlementDefinitionId.getValue());
        return Promise.pure(entitlementDefinition);
    }

    @Override
    public Promise<ResultList<EntitlementDefinition>> getEntitlementDefinitionDefinitions(
            UserId developerId, String type, String group, String tag, PageMetadata pageMetadata) {
        List<EntitlementDefinition> entitlementDefinitions =
                entitlementDefinitionService.getEntitlementDefinitions(
                        developerId.getValue(), group, tag, type, pageMetadata);
        ResultList<EntitlementDefinition> result = new ResultList<EntitlementDefinition>();
        result.setCriteria(entitlementDefinitions);
        if (entitlementDefinitions.size() <
                (pageMetadata.getCount() == null
                        ? EntitlementConsts.DEFAULT_PAGE_SIZE
                        : pageMetadata.getCount())) {
            result.setNext(EntitlementConsts.NEXT_END);
        } else {
            result.setNext(buildNextUrl(developerId.getValue(), type, group, tag, pageMetadata));
        }
        return Promise.pure(result);
    }

    @Override
    public Promise<EntitlementDefinition> postEntitlementDefinition(
            EntitlementDefinition entitlementDefinition) {
        UUID trackingUuid = entitlementDefinition.getTrackingUuid();
        if (trackingUuid != null) {
            EntitlementDefinition existingEntitlementDefinition
                    = entitlementDefinitionService.getByTrackingUuid(trackingUuid);
            if (existingEntitlementDefinition != null) {
                return Promise.pure(existingEntitlementDefinition);
            }
        }
        return Promise.pure(entitlementDefinitionService
                .addEntitlementDefinition(entitlementDefinition));
    }

    @Override
    public Promise<EntitlementDefinition> updateEntitlementDefinition(
            EntitlementDefinitionId entitlementDefinitionId, EntitlementDefinition entitlementDefinition) {
        UUID trackingUuid = entitlementDefinition.getTrackingUuid();
        if (trackingUuid != null) {
            EntitlementDefinition existingEntitlementDefinition
                    = entitlementDefinitionService.getByTrackingUuid(trackingUuid);
            if (existingEntitlementDefinition != null) {
                return Promise.pure(existingEntitlementDefinition);
            }
        }
        return Promise.pure(entitlementDefinitionService
                .updateEntitlementDefinition(entitlementDefinitionId.getValue(), entitlementDefinition));
    }

    @Override
    public Promise<Response> deleteEntitlementDefinition(EntitlementDefinitionId entitlementDefinitionId) {
        entitlementDefinitionService.deleteEntitlement(entitlementDefinitionId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    private String buildNextUrl(Long developerId,
                                String type, String group,
                                String tag, PageMetadata pageMetadata) {
        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("entitlementDefinitions").queryParam("developerId", developerId);
        if (!StringUtils.isEmpty(type)) {
            builder = builder.queryParam("type", type);
        }
        if (!StringUtils.isEmpty(group)) {
            builder = builder.queryParam("group", group);
        }
        if (!StringUtils.isEmpty(tag)) {
            builder = builder.queryParam("tag", tag);
        }
        builder = CommonUtils.buildPageParams(builder,
                pageMetadata.getStart(), pageMetadata.getCount());
        return builder.toTemplate();
    }
}
