/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.list.UserGroupListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserGroupService {
    UserGroup save(UserId userId, UserGroup userGroup);
    UserGroup update(UserId userId, UserGroupId userGroupId, UserGroup userGroup);
    UserGroup patch(UserId userId, UserGroupId userGroupId, UserGroup userGroup);
    UserGroup get(UserId userId, UserGroupId userGroupId);
    List<UserGroup> search(UserGroupListOptions getOption);
    void delete(UserId userId, UserGroupId userGroupId);
}
