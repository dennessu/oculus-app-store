/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.rest.resource;

import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.core.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.UserEntitlementResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Impl of UserEntitlementResource.
 */
public class UserEntitlementResourceImpl implements UserEntitlementResource {
    @Autowired
    private EntitlementService entitlementService;
    @Autowired
    private UriInfo uriInfo;

    @Override
    public Promise<Results<Entitlement>> getEntitlements(UserId userId,
                                                         EntitlementSearchParam searchParam,
                                                         PageMetadata pageMetadata) {
        searchParam.setUserId(userId);
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
        UriBuilder builder = uriInfo.getBaseUriBuilder().path("users")
                .path(IdFormatter.encodeId(searchParam.getUserId()))
                .path("entitlements");
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
}
