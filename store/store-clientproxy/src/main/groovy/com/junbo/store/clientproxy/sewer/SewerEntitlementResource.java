/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.sewer;

import com.junbo.common.id.EntitlementId;
import com.junbo.common.model.Results;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.sewer.entitlement.SewerEntitlement;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The SewerEntitlementResource class.
 */
@Path("/")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
public interface SewerEntitlementResource {

    @Path("entitlements/{entitlementId}")
    @GET
    Promise<SewerEntitlement> getEntitlement(@PathParam("entitlementId") EntitlementId entitlementId,
                                        @QueryParam("expand") String expand, @QueryParam("locale") String locale);

    @Path("entitlements")
    @GET
    Promise<Results<SewerEntitlement>> searchEntitlements(@BeanParam EntitlementSearchParam searchParam, @BeanParam PageMetadata pageMetadata,
                                                     @QueryParam("expand") String expand, @QueryParam("locale") String locale);
}
