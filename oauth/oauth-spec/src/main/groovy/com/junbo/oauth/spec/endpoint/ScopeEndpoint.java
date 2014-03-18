/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.Scope;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * ScopeEndpoint.
 */
@Path("scopes")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ScopeEndpoint {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Scope> postScope(@HeaderParam("Authorization") String authorization, Scope scope);

    @GET
    @Path("/{scopeName}")
    Promise<Scope> getScope(@HeaderParam("Authorization") String authorization,
                            @PathParam("scopeName") String scopeName);

    @GET
    Promise<List<Scope>> getByScopeNames(@HeaderParam("Authorization") String authorization,
                                         @QueryParam("scopeNames") String scopeNames);

    @PUT
    @Path("/{scopeName}")
    @Consumes(MediaType.APPLICATION_JSON)
    Promise<Scope> putScope(@HeaderParam("Authorization") String authorization,
                            @PathParam("scopeName") String scopeName,
                            Scope scope);
}
