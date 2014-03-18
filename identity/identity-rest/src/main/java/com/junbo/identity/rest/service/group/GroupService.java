/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.group;

import com.junbo.common.id.GroupId;
import com.junbo.identity.spec.model.options.GroupGetOption;
import com.junbo.identity.spec.model.users.Group;

import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupService {
    Group get(GroupId groupId);
    Group create(Group group);
    Group update(GroupId groupId, Group group);
    Group patch(GroupId groupId, Group group);
    List<Group> search(GroupGetOption getOption);
}
