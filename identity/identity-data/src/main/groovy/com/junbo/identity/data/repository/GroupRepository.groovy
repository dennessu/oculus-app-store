/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.GroupId
import com.junbo.identity.spec.v1.model.Group
import com.junbo.langur.core.promise.Promise

/**
 * Created by liangfu on 3/14/14.
 */
interface GroupRepository extends IdentityBaseRepository<Group, GroupId> {
    Promise<Group> searchByName(String name)
}
