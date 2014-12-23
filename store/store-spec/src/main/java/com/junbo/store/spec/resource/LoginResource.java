/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.browse.document.Tos;
import com.junbo.store.spec.model.login.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The wrapper api for store front.
 */
@Path("/horizon-api/id")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@AuthorizationNotRequired
public interface LoginResource {
    @GET
    @Path("/check-email")
    @Consumes()
    // This doesn't require email verification
    Promise<EmailCheckResponse> checkEmail(@BeanParam EmailCheckRequest emailCheckRequest);

    @GET
    @Path("/check-username")
    @Consumes()
    // This doesn't require email verification
    Promise<UserNameCheckResponse> checkUsernameAvailable(@BeanParam UserNameCheckRequest userNameCheckRequest);

    @POST
    @Path("/rate-credential")
    // This doesn't require email verification
    Promise<UserCredentialRateResponse> rateUserCredential(UserCredentialRateRequest userCredentialRateRequest);

    @POST
    @Path("/register")
    // This doesn't require email verification
    Promise<AuthTokenResponse> createUser(CreateUserRequest createUserRequest);

    @POST
    @Path("/log-in")
    // This doesn't require email verification
    Promise<AuthTokenResponse> signIn(UserSignInRequest userSignInRequest);

    @POST
    @Path("/refresh-token")
    // This doesn't require email verification
    Promise<AuthTokenResponse> getAuthToken(AuthTokenRequest tokenRequest);

    @POST
    @Path("/confirm-email")
    // This doesn't require email verification
    Promise<ConfirmEmailResponse> confirmEmail(ConfirmEmailRequest confirmEmailRequest);

    @GET
    @Path("/tos")
    @Consumes()
    // This doesn't require email verification
    Promise<Tos> getRegisterTos();

    @GET
    @Path("/countries")
    @Consumes()
    // This doesn't require email verification
    Promise<GetSupportedCountriesResponse> getSupportedCountries();
}
