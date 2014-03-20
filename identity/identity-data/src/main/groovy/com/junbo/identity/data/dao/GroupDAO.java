/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.group.GroupEntity;
import com.junbo.identity.spec.options.GroupListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
public interface GroupDAO extends BaseDao<GroupEntity, Long> {
    // Todo:    need to build reverse lookup table.
    List<GroupEntity> search(GroupListOptions getOption);
}
