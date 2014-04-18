/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.restriction.spec.model.AgeCheck;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 *RestrictionResource.
 */
@Api(value = "restrictions")
@Path("/restrictions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource

public interface RestrictionResource {

    @ApiOperation("Age gate check")
    @GET
    @Path("/age-check")
    Promise<AgeCheck> getAgeCheck(@BeanParam AgeCheck ageCheck);
}
