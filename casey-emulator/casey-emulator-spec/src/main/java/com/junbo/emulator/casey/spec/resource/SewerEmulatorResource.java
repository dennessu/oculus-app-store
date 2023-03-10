/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.emulator.casey.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.store.clientproxy.sewer.SewerEntitlementResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The SewerEmulatorResource class.
 */
@Path("/emulator/sewer/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@AuthorizationNotRequired
public interface SewerEmulatorResource extends SewerEntitlementResource {
}
