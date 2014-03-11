/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.category.Category;
import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.id.Id;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Category resource definition.
 * @deprecated will be abandoned
 */
@Path("categories")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@Deprecated
public interface CategoryResource {
    @GET
    @Path("/")
    Promise<ResultList<Category>> getCategories(@BeanParam EntitiesGetOptions options);

    @GET
    @Path("/{categoryId}")
    Promise<Category> getCategory(@PathParam("categoryId") Id categoryId, @BeanParam EntityGetOptions options);

    /**
     * Create a draft category, the created category is not purchasable until it is released.
     *
     * @param category the category to be created.
     * @return the created category.
     */
    @POST
    @Path("/")
    Promise<Category> create(@Valid Category category);

    @PUT
    @Path("/{categoryId}")
    Promise<Category> update(@PathParam("categoryId") Id categoryId, @Valid Category category);

    /**
     * Developer submit an draft category for review.
     * @param categoryId the id of the category to be reviewed.
     * @return the category to be reviewed.
     */
    @POST
    @Path("/{categoryId}/review")
    Promise<Category> review(@PathParam("categoryId") Id categoryId);

    /**
     * Admin publishes an category, makes it purchasable.
     * @param categoryId the id of category to be released.
     * @return the category to be released.
     */
    @POST
    @Path("/{categoryId}/release")
    Promise<Category> release(@PathParam("categoryId") Id categoryId);

    /**
     * Admin rejects an category, developer may update and submit review later.
     * @param categoryId the id of category to be released.
     * @return the category to be released.
     */
    // TODO: add review notes
    @POST
    @Path("/{categoryId}/reject")
    Promise<Category> reject(@PathParam("categoryId") Id categoryId);

    /**
     * Remove an released category. The draft version is still kept.
     * Developer may update and submit review again in future.
     * @param categoryId the id of category to be removed.
     * @return the removed category id.
     */
    @DELETE
    @Path("/{categoryId}/release")
    Promise<Void> remove(@PathParam("categoryId") Id categoryId);

    /**
     * Delete an category, delete both draft and released version.
     * Developer cannot operate this category again in future.
     * @param categoryId the id of category to be deleted.
     * @return the deleted category id.
     */
    @DELETE
    @Path("/{categoryId}")
    Promise<Void> delete(@PathParam("categoryId") Id categoryId);
}
