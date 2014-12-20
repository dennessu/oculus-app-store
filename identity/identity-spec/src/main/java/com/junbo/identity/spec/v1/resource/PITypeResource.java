/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.PITypeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.PIType;
import com.junbo.identity.spec.v1.option.list.PITypeListOptions;
import com.junbo.identity.spec.v1.option.model.PITypeGetOptions;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by haomin on 14-4-25.
 */
@Api(value = "payment-instrument-types")
@RestResource
@InProcessCallable
@Path("/payment-instrument-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface PITypeResource {
    @ApiOperation("Create a payment instrument type")
    @POST
    Promise<PIType> create(PIType locale);

    @ApiOperation("Update a payment instrument type")
    @PUT
    @Path("/{piTypeId}")
    Promise<PIType> put(@PathParam("piTypeId") PITypeId piTypeId, PIType piType);

    @ApiOperation("Get a payment instrument type")
    @GET
    @Path("/{piTypeId}")
    @AuthorizationNotRequired
    Promise<PIType> get(@PathParam("piTypeId") PITypeId piTypeId, @BeanParam PITypeGetOptions getOptions);

    @ApiOperation("Get all payment instrument type")
    @GET
    @AuthorizationNotRequired
    Promise<Results<PIType>> list(@BeanParam PITypeListOptions listOptions);

    @ApiOperation("Delete a payment instrument type")
    @DELETE
    @Path("/{piTypeId}")
    Promise<Response> delete(@PathParam("piTypeId") PITypeId piTypeId);
}
