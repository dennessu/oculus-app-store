/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;

/**
 * @author Jason
 * time 6/10/2014
 * Interface for Organization related API, including get/post/put/delete organization.
 */
public interface OrganizationService {
    Organization postDefaultOrganization() throws Exception;
    Organization postDefaultOrganization(String userId) throws Exception;
    Organization postOrganization(Organization organization) throws Exception;
    Organization postOrganization(Organization organization, int expectedResponseCode) throws Exception;
    Organization getOrganization(OrganizationId organizationId, int expectedResponseCode) throws Exception;
}
