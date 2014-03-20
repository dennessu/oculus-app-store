/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.spec.options.UserPhoneNumberListOptions;
import com.junbo.sharding.annotations.SeedParam;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserPhoneNumberDAO extends BaseDao<UserPhoneNumberEntity, Long> {

    List<UserPhoneNumberEntity> search(@SeedParam Long userId, UserPhoneNumberListOptions getOption);

    void delete(@SeedParam Long id);
}
