/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.v1.mapper

import com.junbo.common.id.UserId

/**
 * Created by liangfu on 3/14/14.
 */
class CommonMapper {

    public Long toUserId(UserId user) {
        if(user == null) {
            return null;
        }
        return user.getValue();
    }
}
