/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.clientproxy;

import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;

/**
 * Organization facade.
 */
public interface OrganizationFacade {
    Organization getOrganization(OrganizationId organizationId);
}
