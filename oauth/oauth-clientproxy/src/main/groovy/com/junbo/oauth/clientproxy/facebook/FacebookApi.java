/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.clientproxy.facebook;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * FacebookApi.
 */
@RestResource
@Path("/")
public interface FacebookApi {
    @GET
    @Path("/me")
    Promise<FacebookAccount> getAccountInfo(@QueryParam("access_token") String accessToken);
}
