/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.reverselookup.UserNameReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/18/14.
 */
public interface UserNameReverseIndexDAO {
    UserNameReverseIndexEntity save(UserNameReverseIndexEntity entity);
    UserNameReverseIndexEntity update(UserNameReverseIndexEntity entity);
    UserNameReverseIndexEntity get(@SeedParam String userName);
    void delete(@SeedParam String userName);
}
