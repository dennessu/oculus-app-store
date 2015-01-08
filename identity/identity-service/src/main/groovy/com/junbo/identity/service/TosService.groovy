package com.junbo.identity.service

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface TosService {
    Promise<Tos> get(TosId id)

    Promise<Tos> create(Tos model)

    Promise<Tos> update(Tos model, Tos oldModel)

    Promise<Void> delete(TosId id)

    Promise<List<Tos>> searchAll(Integer limit, Integer offset)

    Promise<List<Tos>> searchByType(String type, Integer limit, Integer offset)

    Promise<List<Tos>> searchByState(String state, Integer limit, Integer offset)

    Promise<List<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset)

    Promise<List<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset)

    Promise<List<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset)

    Promise<List<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset)

    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset)
}