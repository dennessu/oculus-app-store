/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign;
import com.junbo.store.spec.model.external.casey.cms.CmsContent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The CaseyEmulatorConfigResource class.
 */
@Path("/emulator/casey")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@AuthorizationNotRequired
public interface CaseyEmulatorConfigResource {

    @POST
    @Path("/reset")
    Promise<CmsContent> reset();

    @POST
    @Path("/cmsCampaign")
    Promise<CmsCampaign> addCmsCampaign(CmsCampaign cmsCampaign);

    @POST
    @Path("/cmsContent")
    Promise<CmsContent> addCmsContent(CmsContent cmsContent);

    @POST
    @Path("/aggregateRating")
    Promise<CaseyAggregateRating> addCaseyAggregateRating(CaseyAggregateRating caseyAggregateRating);
}
