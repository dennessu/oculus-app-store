/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.identity.spec.model.options.GroupGetOption

/**
 * Created by liangfu on 3/14/14.
 */
interface GroupDAO {
    GroupEntity get(Long groupId)
    GroupEntity save(GroupEntity group)
    GroupEntity update(GroupEntity group)
    List<GroupEntity> search(GroupGetOption getOption)
}
