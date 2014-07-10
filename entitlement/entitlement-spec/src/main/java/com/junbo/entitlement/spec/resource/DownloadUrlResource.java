/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.ItemId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * API for downloadUrl.
 */
@Api("item-binary")
@Path("/item-binary")
@RestResource
public interface DownloadUrlResource {
    @ApiOperation("Get preSigned downloadUrl for an item")
    @GET
    @Path("/{itemId}")
    Promise<Response> getDownloadUrl(@QueryParam("entitlementId") EntitlementId entitlementId, @PathParam("itemId") ItemId itemId, @QueryParam("platform") String platform);
}
