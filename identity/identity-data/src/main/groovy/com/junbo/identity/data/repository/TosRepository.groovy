/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Tos
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
    Promise<List<Tos>> searchByType(String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByState(String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset)
}