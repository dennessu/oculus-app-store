/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.common.id.AppId;
import com.junbo.identity.spec.model.app.App;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * Java cod for APP resource.
 */
@RestResource
@Path("identity/apps")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface AppResource {
    @GET
    @Path("/{appId}")
    Promise<App> getApp(@PathParam("appId") AppId appId);

    @POST
    @Path("/")
    Promise<App> postApp(App app);

    @PUT
    @Path("/{appId}")
    Promise<App> updateApp(@PathParam("appId") AppId appId, App app);

    @DELETE
    @Path("/{appId}")
    Promise<App> deleteApp(@PathParam("appId") AppId appId);
}
