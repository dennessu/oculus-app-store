/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao;

import com.junbo.identity.data.entity.user.UserOptinEntity;
import com.junbo.identity.spec.options.list.UserOptinListOption;

import java.util.List;

/**
 * Created by xiali_000 on 3/18/14.
 */
public interface UserOptinDAO {
    UserOptinEntity save(UserOptinEntity entity);
    UserOptinEntity update(UserOptinEntity entity);
    UserOptinEntity get(Long id);
    List<UserOptinEntity> search(Long userId, UserOptinListOption getOption);
    void delete(Long id);
}
