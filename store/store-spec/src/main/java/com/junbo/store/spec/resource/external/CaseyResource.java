/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.casey.CaseyResults;
import com.junbo.store.spec.model.external.casey.OfferSearchParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The CaseyResource interface.
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CaseyResource {

    @GET
    @Path("/search")
    Promise<CaseyResults<JsonNode>> searchOffers(@BeanParam OfferSearchParams params);

}
