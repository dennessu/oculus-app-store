/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.UserPhoneNumberListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserPhoneNumberRepository {
    UserPhoneNumber save(UserPhoneNumber entity);

    UserPhoneNumber update(UserPhoneNumber entity);

    UserPhoneNumber get(UserPhoneNumberId id);

    List<UserPhoneNumber> search(UserPhoneNumberListOptions getOption);

    void delete(UserPhoneNumberId id);
}
