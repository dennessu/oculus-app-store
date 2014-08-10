/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
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

    Boolean ownedByCurrentUser(String propertyPath) {
        def currentUserId = AuthorizeContext.currentUserId
        if (currentUserId == null) {
            return false
        }

        Group entity = getEntity()
        if (entity != null) {
            if (propertyPath == "organization") {
                try {
                    def organization = (factory as GroupAuthorizeCallbackFactory).organizationResource
                            .get(entity.getOrganizationId(), new OrganizationGetOptions()).get()

                    if (organization != null) {
                        return currentUserId == organization.getOwnerId()
                    }
                } catch (AppErrorException e) {
                    return false
                }
            }
        }

        return false
    }
}
