/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.browse.LibraryResponse;
import com.junbo.store.spec.model.iap.IAPConsumeItemRequest;
import com.junbo.store.spec.model.iap.IAPConsumeItemResponse;
import com.junbo.store.spec.model.iap.IAPItemsResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The IAPResource class.
 */
@Path("/horizon-api/iap")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface IAPResource {

    @GET
    @Path("/library")
    // This requires email verification
    Promise<LibraryResponse> getLibrary();

    @GET
    @Path("/items")
    // This requires email verification
    Promise<IAPItemsResponse> getItems();

    @POST
    @Path("/consume-item")
    // This requires email verification
    Promise<IAPConsumeItemResponse> consumeItem(IAPConsumeItemRequest request);
}
