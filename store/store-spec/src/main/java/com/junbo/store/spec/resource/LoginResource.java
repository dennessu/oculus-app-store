/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.login.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The wrapper api for store front.
 */
@Path("/storeapi/id")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@AuthorizationNotRequired
public interface LoginResource {

    @POST
    @Path("/name-check")
    Promise<UserNameCheckResponse> checkUserName(UserNameCheckRequest userNameCheckRequest);

    @POST
    @Path("/rate-credential")
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest userCredentialRateRequest);

    @POST
    @Path("/create")
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest);

    @POST
    @Path("/sign-in")
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest);

    @POST
    @Path("/token")
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest);
}
