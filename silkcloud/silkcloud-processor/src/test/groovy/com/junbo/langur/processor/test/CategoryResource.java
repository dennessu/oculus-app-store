/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.test;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.processor.model.ResultList;
import com.junbo.langur.processor.model.category.Category;
import com.junbo.langur.processor.model.options.category.CategoryGetOptions;
import com.junbo.langur.processor.model.options.category.CategoryPostOptions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The API flow would be as follows:
 * Design status always in CategoryEntityDAO storage and its revision is 0.
 * Release & Deleted always in CategoryEntityDAO storage with real revision.
 * 1)   When user creates one category, it creates this in CategoryEntityDAO, status design and revision 0;
 * 2)   When user update the category, it will only update the revision 0 in CategoryEntityDAO;
 * 3)   When user decided to release the category,
 *          3.1):   it will delete the previous released category(if exists);
 *          3.2):   get current maximum revision from CategoryEntityDAO and use
 *                  max revision + 1 to release this category;
 *          3.3):   Still keep the category that released in CategoryEntityDAO with revision 0 and design status;
 * 4)   Get API if has no specific parameter, it will always return Released category as default;
 *      If user wants to specific Status, it will return the category; But for the nest categoryId
 *      (eg: parentCategoryId) we will always return the released version.
 * 5)   Any time it will have only one DESIGN revision, one Release revision and multiple Delete revision.
 */
@Path("categories")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CategoryResource {
    /*Get API*/
    @GET
    Promise<ResultList<Category>> getCategories(@BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}")
    Promise<Category> getCategory(@PathParam("categoryId") String categoryId,
                                         @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/draft")
    Promise<Category> getCategoryDraft(@PathParam("categoryId") String categoryId,
                                              @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/children")
    Promise<ResultList<Category>> getCategoryChildren(@PathParam("categoryId") String categoryId,
                                                             @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/descendents")
    Promise<ResultList<Category>> getCategoryDescendents(@PathParam("categoryId") String categoryId,
                                                                @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/parents")
    Promise<ResultList<Category>> getCategoryParents(@PathParam("categoryId") String categoryId,
                                                            @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/hists")
    Promise<ResultList<Category>> getCategoryHists(@PathParam("categoryId") String categoryId,
                                                          @BeanParam CategoryGetOptions getOptions);

    /* Write API */
    @POST
    Promise<Category> createCategory(Category category, @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}")
    Promise<Category> updateCategory(@PathParam("categoryId") String categoryId, Category category,
                                            @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}/release")
    Promise<Category> releaseCategory(@PathParam("categoryId") String categoryId,
                                             @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}/delete")
    Promise<Category> deleteCategory(@PathParam("categoryId") String categoryId,
                                            @BeanParam CategoryPostOptions postOptions);
}
