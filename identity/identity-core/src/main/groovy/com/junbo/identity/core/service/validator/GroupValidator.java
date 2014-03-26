/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator;

import com.junbo.common.id.GroupId;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.identity.spec.options.list.GroupListOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupValidator {
    Promise<Void> validateForGet(GroupId groupId);
    Promise<Void> validateForSearch(GroupListOptions options);
    Promise<Void> validateForSearchUserGroup(GroupId groupId, UserGroupListOptions options);
    Promise<Void> validateForCreate(Group group);
    Promise<Void> validateForUpdate(GroupId groupId, Group group, Group oldGroup);
}
