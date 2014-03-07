/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.resource;

import com.junbo.email.spec.model.Email;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Java code for EmailResource.
 */

@Path("/emails")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface EmailResource {
    @POST
    Promise<Email> postEmail(Email email);

    @GET
    @Path("/{id}")
    Promise<Email> getEmail(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    Promise<Email> putEmail(@PathParam("id") Long id, Email email);

    @DELETE
    @Path("/{id}")
    Promise<Response> deleteEmail(@PathParam("id") Long id);

}
