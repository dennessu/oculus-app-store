/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.resource;

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.resource.external.CaseyResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The CaseyEmulatorResource class.
 */
@Path("/emulator/casey/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@AuthorizationNotRequired
public interface CaseyEmulatorResource extends CaseyResource {

    @POST
    @Path("/data")
    Promise<CaseyEmulatorData> postEmulatorData(CaseyEmulatorData caseyEmulatorData);

}
