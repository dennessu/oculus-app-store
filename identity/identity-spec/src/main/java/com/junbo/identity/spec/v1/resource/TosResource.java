/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.TosId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.identity.spec.v1.option.list.TosListOptions;
import com.junbo.identity.spec.v1.option.model.TosGetOptions;
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
 * Created by liangfu on 4/3/14.
 */
@Api(value = "tos")
@RestResource
@InProcessCallable
@Path("/tos")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface TosResource {

    @ApiOperation("Create a tos")
    @POST
    Promise<Tos> create(Tos tos);

    @POST
    @Path("/{tosId}")
    Promise<Tos> patch(@PathParam("tosId") TosId tosId, Tos tos);

    @ApiOperation("Update a tos")
    @PUT
    @Path("/{tosId}")
    Promise<Tos> put(@PathParam("tosId") TosId tosId, Tos tos);

    @ApiOperation("Get a tos info")
    @GET
    @Path("/{tosId}")
    @AuthorizationNotRequired
    Promise<Tos> get(@PathParam("tosId") TosId tosId, @BeanParam TosGetOptions getOptions);

    @ApiOperation("Search tos info")
    @GET
    @AuthorizationNotRequired
    Promise<Results<Tos>> list(@BeanParam TosListOptions listOptions);

    @ApiOperation("Delete a tos")
    @DELETE
    @Path("/{tosId}")
    Promise<Response> delete(@PathParam("tosId") TosId tosId);
}
