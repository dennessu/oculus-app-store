/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementType;
import com.junbo.catalog.spec.resource.EntitlementTypeResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * entitlementType resource impl.
 */
public class EntitlementTypeResourceImpl implements EntitlementTypeResource {
    @Autowired
    private EntitlementDefinitionService entitlementDefinitionService;
    @Override
    public Promise<EntitlementType> getById(String entitlementType) {
        return Promise.pure(entitlementDefinitionService.getEntitlementType(entitlementType));
    }
}
