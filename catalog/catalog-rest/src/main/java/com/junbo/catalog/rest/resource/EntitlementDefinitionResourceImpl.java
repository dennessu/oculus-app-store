/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

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
    public Promise<Results<EntitlementDefinition>> getEntitlementDefinitionDefinitions(
            UserId developerId, String type, String group, String tag, PageableGetOptions pageMetadata) {
        pageMetadata.ensurePagingValid();
        List<EntitlementDefinition> entitlementDefinitions =
                entitlementDefinitionService.getEntitlementDefinitions(
                        developerId.getValue(), group, tag, type, pageMetadata);
        Results<EntitlementDefinition> result = new Results<EntitlementDefinition>();
        result.setItems(entitlementDefinitions);
        result.setNext(buildNextUrl(developerId.getValue(), type, group, tag, pageMetadata));
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
        Long id = entitlementDefinitionService.createEntitlementDefinition(entitlementDefinition);
        return Promise.pure(entitlementDefinitionService.getEntitlementDefinition(id));
    }

    private Link buildNextUrl(Long developerId,
                                String type, String group,
                                String tag, PageableGetOptions pageMetadata) {
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
        builder = buildPageParams(builder, pageMetadata);

        Link result = new Link();
        result.setHref(builder.toTemplate());
        return result;
    }

    private UriBuilder buildPageParams(UriBuilder builder, PageableGetOptions pageableGetOptions) {
        if (pageableGetOptions == null) {
            pageableGetOptions = new PageableGetOptions();
        }
        return builder.queryParam("start", pageableGetOptions.getStart() + pageableGetOptions.getSize())
                .queryParam("size", pageableGetOptions.getSize());
    }
}
