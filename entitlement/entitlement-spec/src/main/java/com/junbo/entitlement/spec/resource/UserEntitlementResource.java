/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Search entitlements resource.
 */
@Api("entitlements")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
@Path("users/{userId}/entitlements")
public interface UserEntitlementResource {
    @ApiOperation("Search entitlements")
    @GET
    Promise<Results<Entitlement>> getEntitlements(
            @PathParam("userId") UserId userId,
            @BeanParam EntitlementSearchParam searchParam,
            @BeanParam PageMetadata pageMetadata);
}
