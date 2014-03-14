/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.v1.mapper
import com.junbo.identity.data.v1.entity.group.GroupEntity
import com.junbo.identity.spec.v1.model.users.Group
import com.junbo.oom.core.Mapper
import com.junbo.oom.core.MappingContext
/**
 * Model Mapper for wrap entity to model, vice versa.
 */
@Mapper(uses =  CommonMapper.class
)
interface ModelMapper {

    GroupEntity toGroupEntity(Group group, MappingContext context)

    Group toGroup(GroupEntity groupEntity, MappingContext context)
}
