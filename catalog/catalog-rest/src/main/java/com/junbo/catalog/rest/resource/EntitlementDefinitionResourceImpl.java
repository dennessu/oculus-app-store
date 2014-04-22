/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefSearchParams;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    public Promise<Results<EntitlementDefinition>> getEntitlementDefinitions(
            EntitlementDefSearchParams searchParams, PageableGetOptions pageMetadata) {
        pageMetadata.ensurePagingValid();
        List<EntitlementDefinition> entitlementDefinitions =
                entitlementDefinitionService.getEntitlementDefinitions(
                        searchParams.getDeveloperId() == null ? null : searchParams.getDeveloperId().getValue(),
                        searchParams.getClientId(), searchParams.getGroups(), searchParams.getTags(),
                        searchParams.getTypes(), searchParams.getIsConsumable(), pageMetadata);
        Results<EntitlementDefinition> result = new Results<EntitlementDefinition>();
        result.setItems(entitlementDefinitions);
        result.setNext(buildNextUrl(searchParams, pageMetadata));
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

    private Link buildNextUrl(EntitlementDefSearchParams searchParams, PageableGetOptions pageMetadata) {
        UriBuilder builder = uriInfo.getBaseUriBuilder().path("entitlement-definitions");
        if (searchParams.getDeveloperId() != null) {
            builder = builder.queryParam("developerId", IdFormatter.encodeId(searchParams.getDeveloperId()));
        }
        if (!StringUtils.isEmpty(searchParams.getClientId())) {
            builder = builder.queryParam("clientId", searchParams.getClientId());
        }
        if (!CollectionUtils.isEmpty(searchParams.getTypes())) {
            for (String type : searchParams.getTypes()) {
                builder = builder.queryParam("types", type);
            }
        }
        if (!CollectionUtils.isEmpty(searchParams.getGroups())) {
            for (String group : searchParams.getGroups()) {
                builder = builder.queryParam("groups", group);
            }
        }
        if (!CollectionUtils.isEmpty(searchParams.getTags())) {
            for (String tag : searchParams.getTags()) {
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
