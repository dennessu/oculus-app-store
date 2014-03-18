/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.core.service;

import com.junbo.entitlement.clientproxy.catalog.CatalogFacade;
import com.junbo.entitlement.clientproxy.identity.UserFacade;
import com.junbo.entitlement.spec.error.AppErrors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Base service.
 */
public class BaseService {
    @Autowired
    private UserFacade userFacade;
    @Autowired
    private CatalogFacade catalogFacade;

    protected Date currentDate() {
//        return EntitlementContext.now();
        return new Date();
    }

    protected void checkUser(Long userId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.missingField("userId").exception();
        }
//        if (!userFacade.exists(userId)) {
//            throw new NotFoundException("user");
//        }
    }

    protected void checkOffer(Long itemId) {
//        if (!catalogFacade.exists(itemId)) {
//            throw new NotFoundException("offer");
//        }
    }

    protected void checkDeveloper(Long developerId) {
        if (developerId == null) {
            throw AppErrors.INSTANCE.missingField("developerId").exception();
        }
//        if (!userFacade.exists(developerId)) {
//            throw new NotFoundException("developer");
//        }
    }

    protected void checkEntitlementDefinition(Long entitlementDefinitionId) {

    }
}
