/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.common.id.GroupId
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.option.model.GroupGetOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * GroupAuthorizeCallbackFactory.
 */
@CompileStatic
class GroupAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<Group> {
    @Override
    AuthorizeCallback<Group> create(Group entity) {
        return new GroupAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<Group> create(GroupId groupId) {
        return create(Promise.get { groupResource.get(groupId, new GroupGetOptions()) })
    }
}
