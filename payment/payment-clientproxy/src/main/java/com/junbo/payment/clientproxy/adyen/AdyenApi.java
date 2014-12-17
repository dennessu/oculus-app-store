/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.adyen;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * adyen rest Api.
 * Note: the authorization is info is a part of the API interface. So it is not using accessTokenProvider.
 */
@RestResource
@Path("/")
@AuthorizationNotRequired
public interface AdyenApi {
    @POST
    @Path("httppost")
    @Produces("text/html")
    Promise<String> authorise(@HeaderParam("Authorization") String auth, String request);
}
