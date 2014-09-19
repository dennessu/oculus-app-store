/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.clientproxy.impl;

import com.junbo.catalog.clientproxy.OrganizationFacade;
import com.junbo.common.id.OrganizationId;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions;
import com.junbo.identity.spec.v1.resource.OrganizationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Organization facade implement.
 */
public class OrganizationFacadeImpl implements OrganizationFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationFacadeImpl.class);
    private OrganizationResource organizationResource;
    private static final OrganizationGetOptions options = new OrganizationGetOptions();

    @Required
    public void setOrganizationResource(OrganizationResource organizationResource) {
        this.organizationResource = organizationResource;
    }

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
