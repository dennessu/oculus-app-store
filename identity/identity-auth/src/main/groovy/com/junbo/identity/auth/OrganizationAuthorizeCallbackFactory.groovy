/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.identity.spec.v1.model.Organization
import groovy.transform.CompileStatic

/**
 * OrganizationAuthorizeCallbackFactory.
 */
@CompileStatic
class OrganizationAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Organization> {
    @Override
    AuthorizeCallback<Organization> create(Organization entity) {
        return new OrganizationAuthorizeCallback(this, entity)
    }
}
