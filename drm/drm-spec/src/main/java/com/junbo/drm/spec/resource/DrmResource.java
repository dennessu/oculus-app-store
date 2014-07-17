/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.spec.resource;

import com.junbo.drm.spec.model.License;
import com.junbo.drm.spec.model.SignedLicense;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * drm.
 */
@Api("/licenses")
@Path("/licenses")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
public interface DrmResource {
    @ApiOperation("Grant a license.")
    @POST
    @Path("/")
    Promise<SignedLicense> postLicense(License request);
}
