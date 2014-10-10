/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 6/6/14.
 */
@Api("migration")
@RestResource
@InProcessCallable
@Path("/imports")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface MigrationResource {
    @ApiOperation("Migration single user")
    @POST
    Promise<OculusOutput> migrate(OculusInput oculusInput);

    @ApiOperation("Migration multiple users")
    @POST
    @Path("/bulk")
    Promise<Map<String, OculusOutput>> bulkMigrate(List<OculusInput> oculusInputs);

    @POST
    @Path("/username-email-block")
    Promise<Response> usernameMailBlock(UsernameMailBlocker usernameMailBlocker);
}
