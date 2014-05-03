/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.google;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

/**
 * GoogleApi.
 */
@RestResource
@Path("/")
public interface GoogleApi {
    @GET
    @Path("/plus/v1/people/me")
    Promise<GoogleAccount> getAccountInfo(@HeaderParam("Authorization") String authorization);
}
