/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.GroupId
import com.junbo.identity.spec.options.list.GroupListOptions
import com.junbo.identity.spec.model.users.Group

/**
 * Created by liangfu on 3/14/14.
 */
interface GroupRepository {
    Group get(GroupId groupId)
    Group save(Group group)
    Group update(Group group)
    List<Group> search(GroupListOptions getOption)
}
