/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator

import com.junbo.common.id.GroupId
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.option.list.GroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/14/14.
 */
@CompileStatic
interface GroupValidator {
    Promise<Group> validateForGet(GroupId groupId)
    Promise<Void> validateForSearch(GroupListOptions options)
    Promise<Void> validateForCreate(Group group)
    Promise<Void> validateForUpdate(GroupId groupId, Group group, Group oldGroup)
}
