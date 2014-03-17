/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.spec.model.options.UserPhoneNumberGetOption;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserPhoneNumberDAO {
    UserPhoneNumberEntity save(UserPhoneNumberEntity entity);

    UserPhoneNumberEntity update(UserPhoneNumberEntity entity);

    UserPhoneNumberEntity get(Long id);

    List<UserPhoneNumberEntity> search(UserPhoneNumberGetOption getOption);

    void delete(Long id);
}
