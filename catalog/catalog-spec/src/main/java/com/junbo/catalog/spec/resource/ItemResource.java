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
    Promise<Item> getItem(@PathParam("ItemId") Long itemId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft item.
     *
     * @param item the Item to be created.
     * @return the created Item.
     */
    @POST
    @Path("/")
    Promise<Item> createItem(@Valid Item item);

    @PUT
    @Path("/{ItemId}")
    Promise<Item> updateItem(@Valid Item item);

    /**
     * Developer submit an draft Item for review.
     * @param itemId the id of the Item to be reviewed.
     * @return the Item to be reviewed.
     */
    @POST
    @Path("/{ItemId}/review")
    Promise<Item> createReview(@PathParam("ItemId") Long itemId);

    /**
     * Admin releases an item.
     * @param itemId the id of Item to be released.
     * @return the Item to be released.
     */
    @POST
    @Path("/{ItemId}/release")
    Promise<Item> releaseItem(@PathParam("ItemId") Long itemId);

    /**
     * Admin rejects an item, developer may update and submit review later.
     * @param itemId the id of Item to be released.
     * @return the Item to be released.
     */
    // TODO: add review notes
    @POST
    @Path("/{ItemId}/reject")
    Promise<Item> rejectItem(@PathParam("ItemId") Long itemId);

    /**
     * Remove the item from released items list. The draft version is still kept.
     * Developer may update and submit review again in future.
     * @param itemId the id of item to be removed.
     * @return the removed Item id.
     */
    @DELETE
    @Path("/{ItemId}/release")
    Promise<Long> removeItem(@PathParam("ItemId") Long itemId);

    /**
     * Delete an Item, delete both draft and released version.
     * Developer cannot operate this itm again in future.
     * @param itemId the id of item to be deleted.
     * @return the deleted item id.
     */
    @DELETE
    @Path("/{ItemId}")
    Promise<Long> deleteItem(@PathParam("ItemId") Long itemId);
}

