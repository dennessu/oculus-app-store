/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.sewer;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.sewer.catalog.SewerItem;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The SewerItemResource class.
 */
@Path("items")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
public interface SewerItemResource {

    @GET
    @Path("/{itemId}")
    Promise<SewerItem> getItem(@PathParam("itemId") String itemId,
                               @QueryParam("expand") String expand, @QueryParam("locale") String locale);

}
