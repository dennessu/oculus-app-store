/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.v1.service.validate;

import com.junbo.common.id.GroupId;
import com.junbo.identity.spec.v1.model.users.Group;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupValidator {
    public void validateGet(GroupId groupId);
    public void validateCreate(Group group);
    public void validateUpdate(GroupId groupId, Group group);
    public void validateDelete(GroupId groupId);
}
