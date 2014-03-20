/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.common.id.ItemId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Item resource definition.
 */
@Api("items")
@Path("items")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ItemResource {

    @ApiOperation("Get or search items")
    @GET
    @Path("/")
    Promise<ResultList<Item>> getItems(@BeanParam ItemsGetOptions options);

    @ApiOperation("Get an item")
    @GET
    @Path("/{itemId}")
    Promise<Item> getItem(@PathParam("itemId") ItemId itemId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft item.
     *
     * @param item the Item to be created.
     * @return the created Item.
     */
    @ApiOperation("Create an item")
    @POST
    @Path("/")
    Promise<Item> create(Item item);

    @ApiOperation("Put an item")
    @PUT
    @Path("/{itemId}")
    Promise<Item> update(@PathParam("itemId") ItemId itemId, Item item);
}

