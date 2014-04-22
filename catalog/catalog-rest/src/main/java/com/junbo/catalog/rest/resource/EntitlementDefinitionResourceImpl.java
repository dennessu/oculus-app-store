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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Set;
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
    public Promise<Results<EntitlementDefinition>> getEntitlementDefinitions(
            UserId developerId, String clientId, String type,
            Set<String> groups, Set<String> tags, Boolean isConsumable, PageableGetOptions pageMetadata) {
        pageMetadata.ensurePagingValid();
        List<EntitlementDefinition> entitlementDefinitions =
                entitlementDefinitionService.getEntitlementDefinitions(
                        developerId.getValue(), clientId, groups, tags, type, isConsumable, pageMetadata);
        Results<EntitlementDefinition> result = new Results<EntitlementDefinition>();
        result.setItems(entitlementDefinitions);
        result.setNext(buildNextUrl(developerId.getValue(), clientId, type, groups, tags, pageMetadata));
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

    @Override
    public Promise<Response> deleteEntitlementDefinition(EntitlementDefinitionId entitlementDefinitionId) {
        entitlementDefinitionService.deleteEntitlement(entitlementDefinitionId.getValue());
        return Promise.pure(Response.status(204).build());

    }

    @Override
    public Promise<EntitlementDefinition> updateEntitlementDefinition(EntitlementDefinitionId entitlementDefinitionId, EntitlementDefinition entitlementDefinition) {
        UUID trackingUuid = entitlementDefinition.getTrackingUuid();
        if (trackingUuid != null) {
            EntitlementDefinition existingEntitlementDefinition
                    = entitlementDefinitionService.getByTrackingUuid(trackingUuid);
            if (existingEntitlementDefinition != null) {
                return Promise.pure(existingEntitlementDefinition);
            }
        }
        Long id = entitlementDefinitionService
                .updateEntitlementDefinition(entitlementDefinitionId.getValue(), entitlementDefinition);
        return Promise.pure(entitlementDefinitionService.getEntitlementDefinition(id));
    }

    private Link buildNextUrl(Long developerId, String clientId,
                              String type, Set<String> groups,
                              Set<String> tags, PageableGetOptions pageMetadata) {
        UriBuilder builder = uriInfo.getBaseUriBuilder()
                .path("entitlement-definitions").queryParam("developerId", developerId);
        if (!StringUtils.isEmpty(clientId)) {
            builder = builder.queryParam("clientId", clientId);
        }
        if (!StringUtils.isEmpty(type)) {
            builder = builder.queryParam("type", type);
        }
        if (!CollectionUtils.isEmpty(groups)) {
            for (String group : groups){
                builder = builder.queryParam("groups", group);
            }
        }
        if (!CollectionUtils.isEmpty(tags)) {
            for (String tag : tags){
                builder = builder.queryParam("tags", tag);
            }
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
