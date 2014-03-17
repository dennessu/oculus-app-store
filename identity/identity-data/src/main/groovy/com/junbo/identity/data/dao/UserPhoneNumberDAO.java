/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.options.UserPhoneNumberGetOption;
import com.junbo.identity.spec.model.users.UserPhoneNumber;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
public interface UserPhoneNumberDAO {
    UserPhoneNumber save(UserPhoneNumber entity);

    UserPhoneNumber update(UserPhoneNumber entity);

    UserPhoneNumber get(UserPhoneNumberId id);

    List<UserPhoneNumber> search(UserPhoneNumberGetOption getOption);

    void delete(UserPhoneNumberId id);
}
