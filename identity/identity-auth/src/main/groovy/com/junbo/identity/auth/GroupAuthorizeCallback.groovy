/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.identity.spec.v1.model.Group
import groovy.transform.CompileStatic

/**
 * GroupAuthorizeCallback.
 */
@CompileStatic
class GroupAuthorizeCallback extends AbstractAuthorizeCallback<Group> {
    @Override
    String getApiName() {
        return 'groups'
    }

    GroupAuthorizeCallback(GroupAuthorizeCallbackFactory factory, Group entity) {
        super(factory, entity)
    }

    @Override
    protected Object getEntityIdByPropertyPath(String propertyPath) {
        Group entity = getEntity()
        if (entity != null) {
            if ("organization".equals(propertyPath)) {
                return getEntity().getOrganizationId();
            }

            return super.getEntityIdByPropertyPath(propertyPath);
        }

        return null
    }
}
