/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.crypto.spec.model.UserCryptoKey;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 5/6/14.
 */
@RestResource
@InProcessCallable
@Path("/crypto")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCryptoResource {

    // Once a user is defined, its user key is determined.
    // No user can access this resource any more.
    // If user already has user key, it just increase the version, then insert the new encrypt value.
    @POST
    Promise<Void> create(UserCryptoKey userCryptoKey);
}
