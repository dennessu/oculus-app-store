/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.resource;

import com.junbo.authorization.spec.model.ApiDefinition;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * ApiDefinitionResource.
 */
@Api("authorization")
@Path("/api-definitions")
@RestResource
@InProcessCallable
@Produces(MediaType.APPLICATION_JSON)
@Consumes({MediaType.APPLICATION_JSON})
public interface ApiDefinitionResource {

    @ApiOperation("Get an Api Definition by name")
    @GET
    @Path("/{apiName}")
    Promise<ApiDefinition> get(@PathParam("apiName") String apiName);
}
