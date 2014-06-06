/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.authorization

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import groovy.transform.CompileStatic

/**
 * Created by Zhanxin on 5/23/2014.
 */
@CompileStatic
public interface OwnerCallback {
    UserId getUserOwnerId(Id resourceId)
}