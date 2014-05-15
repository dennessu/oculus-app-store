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
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * ApiEndpoint.
 */
@Api("oauth2")
@Path("/oauth2/apis")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface ApiEndpoint {
    @GET
    Promise<List<ApiDefinition>> getAllApis();

    @GET
    @Path("/{apiName}")
    Promise<ApiDefinition> getApi(@PathParam("apiName") String apiName);

    @POST
    Promise<ApiDefinition> postApi(ApiDefinition apiDefinition);

    @PUT
    @Path("/{apiName}")
    Promise<ApiDefinition> putApi(@PathParam("apiName") String apiName, ApiDefinition apiDefinition);

    @DELETE
    @Path("/{apiName}")
    Promise<Void> deleteApi(@PathParam("apiName") String apiName);
}
