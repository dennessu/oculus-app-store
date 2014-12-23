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
    Promise<List<Tos>> searchByTitle(String title, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByType(String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByState(String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndType(String title, String type, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndState(String title, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndCountry(String title, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndTypeAndState(String title, String type, String state, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndTypeAndCountry(String title, String type, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndStateAndCountry(String title, String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset)

    @ReadMethod
    Promise<List<Tos>> searchByTitleAndTypeAndStateAndCountry(String title, String type, String state, CountryId country, Integer limit, Integer offset)
}