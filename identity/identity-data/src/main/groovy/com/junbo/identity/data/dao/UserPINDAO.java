/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.common.id.UserPINId;
import com.junbo.identity.spec.model.options.UserPinGetOption;
import com.junbo.identity.spec.model.users.UserPIN;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPINDAO {

    UserPIN save(UserPIN entity);

    UserPIN update(UserPIN entity);

    UserPIN get(UserPINId id);

    List<UserPIN> search(UserPinGetOption getOption);

    void delete(UserPINId id);
}
