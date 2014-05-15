/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.dualwrite

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.langur.core.promise.Promise

import java.lang.reflect.Method

/**
 * DataAccessStrategy.
 */
interface DataAccessStrategy {
    Promise<CloudantEntity> invokeReadMethod(Method method, Object[] args)

    Promise<CloudantEntity> invokeWriteMethod(Method method, Object[] args)

    Promise<Void> invokeDeleteMethod(Method method, Object[] args)
}
