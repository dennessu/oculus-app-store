package com.junbo.identity.service.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.service.TosService
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class TosServiceImpl implements TosService {
    private TosRepository tosRepository

    @Override
    Promise<Tos> get(TosId id) {
        return tosRepository.get(id)
    }

    @Override
    Promise<Tos> create(Tos model) {
        return tosRepository.create(model)
    }

    @Override
    Promise<Tos> update(Tos model, Tos oldModel) {
        return tosRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(TosId id) {
        return tosRepository.delete(id)
    }

    @Override
    Promise<List<Tos>> searchAll(Integer limit, Integer offset) {
        return tosRepository.searchAll(limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByType(String type, Integer limit, Integer offset) {
        return tosRepository.searchByType(type, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByState(String state, Integer limit, Integer offset) {
        return tosRepository.searchByState(state, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset) {
        return tosRepository.searchByCountry(country, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset) {
        return tosRepository.searchByTypeAndState(type, state, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset) {
        return tosRepository.searchByTypeAndCountry(type, country, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset) {
        return tosRepository.searchByStateAndCountry(state, country, limit, offset)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset) {
        return tosRepository.searchByTypeAndStateAndCountry(type, state, country, limit, offset)
    }

    @Required
    void setTosRepository(TosRepository tosRepository) {
        this.tosRepository = tosRepository
    }
}
