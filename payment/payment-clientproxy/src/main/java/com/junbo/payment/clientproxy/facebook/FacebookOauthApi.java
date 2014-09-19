/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Facebook Oauth Api.
 */
@RestResource
@Path("/")
public interface FacebookOauthApi {
    @GET
    @Path("oauth/access_token")
    Promise<String> getAccessToken(@BeanParam FacebookTokenRequest fbTokenRequest);
}
