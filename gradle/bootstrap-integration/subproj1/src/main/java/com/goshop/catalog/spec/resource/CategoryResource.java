package com.goshop.catalog.spec.resource;

import com.google.common.util.concurrent.ListenableFuture;
import com.goshop.catalog.spec.model.ResultList;
import com.goshop.catalog.spec.model.category.Category;
import com.goshop.catalog.spec.model.category.CategoryHist;
import com.goshop.catalog.spec.model.options.category.CategoryGetOptions;
import com.goshop.catalog.spec.model.options.category.CategoryPostOptions;
import com.goshop.langur.core.RestResource;

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
 *          3.2):   get current maximum revision from CategoryEntityDAO and use max revision + 1 to release this category;
 *          3.3):   Still keep the category that released in CategoryEntityDAO with revision 0 and design status;
 * 4)   Get API if has no specific parameter, it will always return Released category as default;
 *      If user wants to specific Status, it will return the category; But for the nest categoryId (eg: parentCategoryId) we will always return the released version.
 * 5)   Any time it will have only one DESIGN revision, one Release revision and multiple Delete revision.
 */
@Path("categories")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CategoryResource {
    /*Get API*/
    @GET
    public ListenableFuture<ResultList<Category>> getCategories(@BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}")
    public ListenableFuture<Category> getCategory(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/draft")
    public ListenableFuture<Category> getCategoryDraft(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/children")
    public ListenableFuture<ResultList<Category>> getCategoryChildren(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/descendents")
    public ListenableFuture<ResultList<Category>> getCategoryDescendents(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/parents")
    public ListenableFuture<ResultList<Category>> getCategoryParents(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    @GET
    @Path("/{categoryId}/hists")
    public ListenableFuture<ResultList<Category>> getCategoryHists(@PathParam("categoryId") String categoryId, @BeanParam CategoryGetOptions getOptions);

    /* Write API */
    @POST
    public ListenableFuture<Category> createCategory(Category category, @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}")
    public ListenableFuture<Category> updateCategory(@PathParam("categoryId") String categoryId, Category category, @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}/release")
    public ListenableFuture<Category> releaseCategory(@PathParam("categoryId") String categoryId, @BeanParam CategoryPostOptions postOptions);

    @POST
    @Path("/{categoryId}/delete")
    public ListenableFuture<Category> deleteCategory(@PathParam("categoryId") String categoryId, @BeanParam CategoryPostOptions postOptions);
}
