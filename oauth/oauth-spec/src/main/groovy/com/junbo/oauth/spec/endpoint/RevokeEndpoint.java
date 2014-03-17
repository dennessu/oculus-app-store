/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.spec.endpoint;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RevokeEndpoint.
 */
@Path("revoke")
@RestResource
public interface RevokeEndpoint {
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Promise<Response> revoke(@HeaderParam("Authorization") String authorization, @FormParam("token") String token,
                             @FormParam("token_type_hint") String tokenTypeHint);
}
