/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator;

import com.junbo.common.id.GroupId;
import com.junbo.identity.spec.model.users.Group;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupValidator {
    void validateGet(GroupId groupId);
    void validateCreate(Group group);
    void validateUpdate(GroupId groupId, Group group);
    void validateDelete(GroupId groupId);
}
