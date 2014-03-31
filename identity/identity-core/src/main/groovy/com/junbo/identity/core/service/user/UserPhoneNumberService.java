/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.user;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public interface UserPhoneNumberService {
    UserPhoneNumber save(UserId userId, UserPhoneNumber userPhoneNumber);
    UserPhoneNumber update(UserId userId, UserPhoneNumberId userPhoneNumberId, UserPhoneNumber userPhoneNumber);
    UserPhoneNumber get(UserId userId, UserPhoneNumberId userPhoneNumberId);
    List<UserPhoneNumber> search(UserPhoneNumberListOptions listOption);
    void delete(UserId userId, UserPhoneNumberId userPhoneNumberId);
}
