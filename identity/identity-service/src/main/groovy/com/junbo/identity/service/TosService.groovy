package com.junbo.identity.service

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
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

    Promise<Results<Tos>> searchAll(Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitle(String title, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByType(String type, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByState(String state, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndType(String title, String type, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndState(String title, String state, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndCountry(String title, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndTypeAndState(String title, String type, String state, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndTypeAndCountry(String title, String type, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndStateAndCountry(String title, String state, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset)

    Promise<Results<Tos>> searchByTitleAndTypeAndStateAndCountry(String title, String type, String state, CountryId country, Integer limit, Integer offset)
}
