/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.AccessToken;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * AccessTokenResource.
 */
@Api("oauth2")
@Path("/oauth2/access-token")
@RestResource
@AuthorizationNotRequired
@Produces(MediaType.APPLICATION_JSON)
public interface AccessTokenResource {
    @GET
    @Path("/{access-token}")
    Promise<AccessToken> get(@PathParam("access-token") String accessToken);
}
