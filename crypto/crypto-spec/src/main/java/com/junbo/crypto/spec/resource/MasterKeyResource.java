/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.resource;

import com.junbo.crypto.spec.model.MasterKey;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * This API is just for internal usage only. It SHOULD not expose to external partners.
 * Created by liangfu on 5/12/14.
 */
@Api(value = "master-key")
@RestResource
@Path("/master-key")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface MasterKeyResource {
    // This can
    @POST
    Promise<Void> create(MasterKey masterKey);
}
