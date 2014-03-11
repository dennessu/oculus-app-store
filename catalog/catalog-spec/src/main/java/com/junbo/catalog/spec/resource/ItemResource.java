/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.Id;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Item resource definition.
 */
@Path("items")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ItemResource {
    @GET
    @Path("/")
    Promise<ResultList<Item>> getItems(@BeanParam EntitiesGetOptions options);

    @GET
    @Path("/{ItemId}")
    Promise<Item> getItem(@PathParam("ItemId") Id itemId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft item.
     *
     * @param item the Item to be created.
     * @return the created Item.
     */
    @POST
    @Path("/")
    Promise<Item> create(@Valid Item item);

    @PUT
    @Path("/{itemId}")
    Promise<Item> update(@PathParam("itemId") Id itemId, @Valid Item item);
}

