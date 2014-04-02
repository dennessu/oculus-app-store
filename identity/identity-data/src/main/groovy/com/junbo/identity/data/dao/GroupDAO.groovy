/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao
import com.junbo.identity.data.entity.group.GroupEntity
import com.junbo.sharding.annotations.SeedParam
/**
 * Created by liangfu on 3/14/14.
 */
interface GroupDAO {
    GroupEntity get(@SeedParam Long groupId)
    GroupEntity save(GroupEntity group)
    GroupEntity update(GroupEntity group)
    Long findIdByName(@SeedParam String name)
}
