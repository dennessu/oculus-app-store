/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.index;

import com.junbo.identity.data.entity.reverselookup.UserEmailReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/21/14.
 */
public interface UserEmailReverseIndexDAO {
    UserEmailReverseIndexEntity save(UserEmailReverseIndexEntity entity);
    UserEmailReverseIndexEntity update(UserEmailReverseIndexEntity entity);
    UserEmailReverseIndexEntity get(@SeedParam String userName);
    void delete(@SeedParam String userName);
}
