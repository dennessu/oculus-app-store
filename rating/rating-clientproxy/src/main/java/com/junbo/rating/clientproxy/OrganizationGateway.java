/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy;

import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;

/**
 * Created by lizwu on 12/15/14.
 */
public interface OrganizationGateway {
    Organization getOrganization(OrganizationId organizationId);
}


