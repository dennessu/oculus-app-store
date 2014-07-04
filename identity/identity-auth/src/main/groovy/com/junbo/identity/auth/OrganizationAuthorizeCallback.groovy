/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.Organization
import groovy.transform.CompileStatic

/**
 * OrganizationAuthorizeCallback.
 */
@CompileStatic
class OrganizationAuthorizeCallback extends AbstractAuthorizeCallback<Organization> {
    @Override
    String getApiName() {
        return 'organizations'
    }

    OrganizationAuthorizeCallback(OrganizationAuthorizeCallbackFactory factory, Organization entity) {
        super(factory, entity)
    }

    @Override
    protected UserId getUserOwnerId() {
        Organization entity = getEntity()
        if (entity != null) {
            return (UserId) ((Organization) entity).ownerId
        }

        return null
    }
}
