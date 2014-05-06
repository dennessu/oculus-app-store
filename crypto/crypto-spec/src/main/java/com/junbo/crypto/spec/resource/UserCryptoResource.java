/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 5/6/14.
 */
@Api(value = "crypto")
@RestResource
@Path("/crypto")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCryptoResource {

    // Once a user is defined, its user key is determined.
    // No user can access this resource any more.
    // If user already has user key, it can't be updated or recreated.
    @POST
    Promise<String> generateUserEncryptKey(UserId userId);
}
