/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
interface TosRepository extends BaseRepository<Tos, TosId> {
    @ReadMethod
    Promise<List<Tos>> searchAll(Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitle(String title, Integer limit, Integer offset)
}