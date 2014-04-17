/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.spec.resource;

import com.junbo.drm.spec.model.License;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * drm.
 */
@Path("/licenses")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface DrmResource {
    @POST
    @Path("/")
    Promise<License> postLicense(@Valid License request);

}
