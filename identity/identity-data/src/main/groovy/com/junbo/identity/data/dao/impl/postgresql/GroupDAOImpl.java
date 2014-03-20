/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.dao.impl.postgresql;
import com.junbo.identity.data.dao.GroupDAO;
import com.junbo.identity.data.entity.group.GroupEntity;
import com.junbo.identity.spec.options.GroupListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/14/14.
 */
public class GroupDAOImpl extends BaseDaoImpl<GroupEntity, Long> implements GroupDAO {
    @Override
    public List<GroupEntity> search(GroupListOptions getOption) {
        return null;
    }
}
