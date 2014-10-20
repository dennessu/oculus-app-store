/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.oauth;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oauth.spec.model.ViewModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The OAuthEmailVerifyEndpoint class. This is same as {@link com.junbo.oauth.spec.endpoint.EmailVerifyEndpoint} except the return type
 * is not {@link javax.ws.rs.core.Response}. This is a workaround for the issue that the  {@link javax.ws.rs.core.Response} could not be
 * decoded in non-inprocess-call mode.
 */
@Path("/oauth2/verify-email")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
public interface OAuthEmailVerifyEndpoint {

    @GET
    Promise<ViewModel> verifyEmail(@QueryParam("evc") String evc,
                                  @QueryParam("locale") String locale);

    @POST
    Promise<Void> sendVerifyEmail(@FormParam("locale") String locale,
                                      @FormParam("country") String country,
                                      @FormParam("userId") UserId userId,
                                      @FormParam("tml") UserPersonalInfoId targetMail);

    @POST
    @Path("/welcome")
    Promise<Void> sendWelcomeEmail(@FormParam("locale") String locale,
                                       @FormParam("country") String country,
                                       @FormParam("userId") UserId userId);
}
