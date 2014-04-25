/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.enumid.PITypeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.PIType;
import com.junbo.identity.spec.v1.option.list.PITypeListOptions;
import com.junbo.identity.spec.v1.option.model.PITypeGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by haomin on 14-4-25.
 */
@Api(value = "payment-instrument-types")
@RestResource
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
    Promise<PIType> get(@PathParam("piTypeId") PITypeId piTypeId, @BeanParam PITypeGetOptions getOptions);

    @ApiOperation("Get all payment instrument type")
    @GET
    Promise<Results<PIType>> list(@BeanParam PITypeListOptions listOptions);
}
