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
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Category resource definition.
 */
@Path("categories")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CategoryResource {
    @GET
    @Path("/")
    Promise<ResultList<Category>> getCategories(@BeanParam EntitiesGetOptions options);

    @GET
    @Path("/{categoryId}")
    Promise<Category> getCategory(@PathParam("categoryId") Long categoryId, @BeanParam EntityGetOptions options);

    @POST
    @Path("/")
    Promise<Category> createCategory(@Valid Category category);
}
