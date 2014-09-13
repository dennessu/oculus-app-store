/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.userlog

import com.junbo.common.id.UserLogId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by x on 9/2/14.
 */
@CompileStatic
public interface UserLogRepository {
    Promise<UserLog> get(UserLogId id);

    Promise<UserLog> create(UserLog model);

    Results<UserLog> list(UserLogListOptions options)
}