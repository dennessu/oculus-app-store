/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository;

import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.options.list.UserPinListOption;
import com.junbo.identity.spec.model.users.UserPin;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
public interface UserPinRepository {

    UserPin save(UserPin entity);

    UserPin update(UserPin entity);

    UserPin get(UserPinId id);

    List<UserPin> search(UserPinListOption getOption);

    void delete(UserPinId id);
}
