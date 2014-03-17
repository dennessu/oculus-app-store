/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserPINEntity;
import com.junbo.identity.spec.model.options.UserPinGetOption;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPINDAO {

    UserPINEntity save(UserPINEntity entity);

    UserPINEntity update(UserPINEntity entity);

    UserPINEntity get(Long id);

    List<UserPINEntity> search(UserPinGetOption getOption);

    void delete(Long id);
}
