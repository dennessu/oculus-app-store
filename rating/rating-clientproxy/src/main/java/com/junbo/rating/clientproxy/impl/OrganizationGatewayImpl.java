/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions;
import com.junbo.identity.spec.v1.resource.OrganizationResource;
import com.junbo.rating.clientproxy.OrganizationGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by lizwu on 12/15/14.
 */
public class OrganizationGatewayImpl implements OrganizationGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationGatewayImpl.class);
    private static final OrganizationGetOptions options = new OrganizationGetOptions();

    @Autowired
    @Qualifier("ratingOrganizationClient")
    private OrganizationResource organizationResource;

    @Override
    public Organization getOrganization(OrganizationId organizationId) {
        try {
            return organizationResource.get(organizationId, options).get();
        } catch(Exception e) {
            LOGGER.error("Cannot find organization " + organizationId, e);
            return null;
        }
    }
}
