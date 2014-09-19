/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.spec.resource;

import com.junbo.common.id.ItemId;
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions;
import com.junbo.entitlement.spec.model.DownloadUrlResponse;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * API for downloadUrl.
 */
@Api("item-binary")
@Path("/item-binary")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
public interface DownloadUrlResource {
    @ApiOperation("Get preSigned downloadUrl for an item")
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{itemId}")
    Promise<DownloadUrlResponse> getDownloadUrl(@PathParam("itemId") ItemId itemId, @BeanParam DownloadUrlGetOptions options);
}
