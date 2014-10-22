/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.Scope;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * ScopeEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/scopes")
@RestResource
@InProcessCallable
@Produces(MediaType.APPLICATION_JSON)
public interface ScopeEndpoint {

    @ApiOperation("Create a new scope")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Scope> postScope(Scope scope);

    @ApiOperation("Get a scope")
    @GET
    @Path("/{scopeName}")
    Promise<Scope> getScope(@PathParam("scopeName") String scopeName);

    @ApiOperation("Get or search scopes")
    @GET
    Promise<Results<Scope>> getByScopeNames(@QueryParam("scopeNames") String scopeNames);

    @ApiOperation("Put a scope")
    @PUT
    @Path("/{scopeName}")
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Scope> putScope(@PathParam("scopeName") String scopeName, Scope scope);
}
