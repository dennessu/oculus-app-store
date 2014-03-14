/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.service.group;

import com.junbo.common.id.GroupId;
import com.junbo.identity.spec.v1.model.options.GroupGetOption;
import com.junbo.identity.spec.v1.model.users.Group;

import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupService {
    public Group get(GroupId groupId);
    public Group create(Group group);
    public Group update(GroupId groupId, Group group);
    public Group patch(GroupId groupId, Group group);
    public List<Group> search(GroupGetOption getOption);
}
