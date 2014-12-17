package com.junbo.identity.service.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.service.CurrencyService
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class CurrencyServiceImpl implements CurrencyService {

    private CurrencyRepository currencyRepository

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> get(CurrencyId id) {
        return currencyRepository.get(id)
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> create(com.junbo.identity.spec.v1.model.Currency model) {
        return currencyRepository.create(model)
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> update(com.junbo.identity.spec.v1.model.Currency model, com.junbo.identity.spec.v1.model.Currency oldModel) {
        return currencyRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(CurrencyId id) {
        return currencyRepository.delete(id)
    }

    @Override
    Promise<Results<com.junbo.identity.spec.v1.model.Currency>> searchAll(Integer limit, Integer offset) {
        return currencyRepository.searchAll(limit, offset)
    }

    @Required
    void setCurrencyRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository
    }
}
