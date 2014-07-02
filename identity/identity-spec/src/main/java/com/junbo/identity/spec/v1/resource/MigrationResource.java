/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteAnyLocal;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by liangfu on 6/6/14.
 */
@RestResource
@InProcessCallable
@Path("/imports")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface MigrationResource {
    @POST
    @RouteAnyLocal
    Promise<OculusOutput> migrate(OculusInput oculusInput);

    @POST
    @RouteAnyLocal
    @Path("/bulk")
    Promise<Map<String, OculusOutput>> bulkMigrate(List<OculusInput> oculusInputs);
}
