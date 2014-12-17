/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.GroupId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by liangfu on 3/14/14.
 */
interface GroupRepository extends BaseRepository<Group, GroupId> {
    @ReadMethod
    Promise<Results<Group>> searchByOrganizationIdAndName(OrganizationId id, String name, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Group>> searchByOrganizationId(OrganizationId id, Integer limit, Integer offset)
}
