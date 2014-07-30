/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.UserInfo;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Javadoc.
 */
@Api("oauth2")
@Path("/oauth2/userinfo")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@AuthorizationNotRequired
public interface UserInfoEndpoint {

    @ApiOperation("Get the user info associated with the token")
    @GET
    Promise<UserInfo> getUserInfo(@HeaderParam("Authorization") String authorization);
}
