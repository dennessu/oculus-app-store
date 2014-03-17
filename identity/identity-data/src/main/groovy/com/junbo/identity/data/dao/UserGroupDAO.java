/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.common.id.UserGroupId;
import com.junbo.identity.spec.model.options.UserGroupGetOption;
import com.junbo.identity.spec.model.users.UserGroup;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserGroupDAO {
    UserGroup save(UserGroup entity);

    UserGroup update(UserGroup entity);

    UserGroup get(UserGroupId id);

    List<UserGroup> search(UserGroupGetOption getOption);

    void delete(UserGroupId id);
}
