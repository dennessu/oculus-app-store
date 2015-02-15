/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.resource;

import com.junbo.common.id.EmailId;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailSearchOption;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Java code for EmailResource.
 */
@Api(value= "emails")
@Path("/emails")
@InProcessCallable
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface EmailResource {
    @ApiOperation("Create an email sending request")
    @POST
    Promise<Email> postEmail(Email email);

    @ApiOperation("Get an email sending history")
    @GET
    @Path("/{id}")
    Promise<Email> getEmail(@PathParam("id") EmailId id);

    @ApiOperation("Modify the email sending request")
    @PUT
    @Path("/{id}")
    Promise<Email> putEmail(@PathParam("id") EmailId id, Email email);

    @ApiOperation("Delete the email sending request")
    @DELETE
    @Path("/{id}")
    Promise<Response> deleteEmail(@PathParam("id") EmailId id);

    @ApiOperation("search email history")
    @GET
    Promise<Results<Email>> search(@BeanParam EmailSearchOption emailSearchOption);
}
