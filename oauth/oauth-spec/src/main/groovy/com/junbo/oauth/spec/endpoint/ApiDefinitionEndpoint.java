/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.ApiDefinition;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * ApiDefinitionEndpoint.
 */
@Api("authorization")
@Path("/api-definitions")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ApiDefinitionEndpoint {
    @GET
    Promise<List<ApiDefinition>> list();

    @GET
    @Path("/{apiName}")
    Promise<ApiDefinition> get(@PathParam("apiName") String apiName);

    @POST
    Promise<ApiDefinition> create(ApiDefinition apiDefinition);

    @PUT
    @Path("/{apiName}")
    Promise<ApiDefinition> update(@PathParam("apiName") String apiName, ApiDefinition apiDefinition);

    @DELETE
    @Path("/{apiName}")
    Promise<Void> delete(@PathParam("apiName") String apiName);
}
