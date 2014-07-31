/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.common.model.Results;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Item resource definition.
 */
@Api("items")
@Path("items")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface ItemResource {
    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get or search items")
    @GET
    @Path("/")
    @AuthorizationNotRequired
    Promise<Results<Item>> getItems(@BeanParam ItemsGetOptions options);

    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get an item")
    @GET
    @Path("/{itemId}")
    @AuthorizationNotRequired
    Promise<Item> getItem(@PathParam("itemId") String itemId);

    @ApiOperation("Create an item")
    @POST
    @Path("/")
    Promise<Item> create(Item item);

    @ApiOperation("Put an item")
    @PUT
    @Path("/{itemId}")
    Promise<Item> update(@PathParam("itemId") String itemId, Item item);

    @ApiOperation("Delete an item")
    @DELETE
    @Path("/{itemId}")
    Promise<Response> delete(@PathParam("itemId") String itemId);
}

