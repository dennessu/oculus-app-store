/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.common.id.GroupId
import com.junbo.identity.spec.model.options.GroupGetOption
import com.junbo.identity.spec.model.users.Group

/**
 * Created by liangfu on 3/14/14.
 */
interface GroupDAO {
    Group get(GroupId groupId)
    Group save(Group group)
    Group update(Group group)
    List<Group> search(GroupGetOption getOption)
}
