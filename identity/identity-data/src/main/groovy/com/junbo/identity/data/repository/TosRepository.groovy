/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
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
    Promise<Results<Tos>> searchAll(Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitle(String title, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByType(String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByState(String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndType(String title, String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndState(String title, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndCountry(String title, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndTypeAndState(String title, String type, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndTypeAndCountry(String title, String type, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndStateAndCountry(String title, String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Tos>> searchByTitleAndTypeAndStateAndCountry(String title, String type, String state, CountryId country, Integer limit, Integer offset)
}
