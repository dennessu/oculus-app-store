/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.attribute.AttributesGetOptions;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.id.AttributeId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Attribute resource.
 */
@Path("attributes")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface AttributeResource {
    @GET
    @Path("/{attributeId}")
    Promise<Attribute> getAttribute(@PathParam("attributeId") AttributeId attributeId);

    @GET
    @Path("/")
    Promise<ResultList<Attribute>> getAttributes(@BeanParam AttributesGetOptions options);

    @POST
    @Path("/")
    Promise<Attribute> createAttribute(Attribute attribute);
}
