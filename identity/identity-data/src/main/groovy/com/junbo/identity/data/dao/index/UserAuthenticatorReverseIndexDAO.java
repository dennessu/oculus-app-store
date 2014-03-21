/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.index;

import com.junbo.identity.data.entity.reverselookup.UserAuthenticatorReverseIndexEntity;
import com.junbo.sharding.annotations.SeedParam;

/**
 * Created by liangfu on 3/21/14.
 */
public interface UserAuthenticatorReverseIndexDAO {
    UserAuthenticatorReverseIndexEntity save(UserAuthenticatorReverseIndexEntity entity);
    UserAuthenticatorReverseIndexEntity update(UserAuthenticatorReverseIndexEntity entity);
    UserAuthenticatorReverseIndexEntity get(@SeedParam String value);
    void delete(@SeedParam String value);
}
