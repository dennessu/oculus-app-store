/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.index;

import com.junbo.identity.data.entity.reverselookup.GroupReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/21/14.
 */
public interface GroupReverseIndexDAO {
    GroupReverseIndexEntity save(GroupReverseIndexEntity entity);
    GroupReverseIndexEntity update(GroupReverseIndexEntity entity);
    GroupReverseIndexEntity get(@SeedParam String name);
    void delete(@SeedParam String name);
}
