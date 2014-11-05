package com.junbo.identity.service.impl

import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.service.LocaleService
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class LocaleServiceImpl implements LocaleService {
    private LocaleRepository localeRepository

    @Override
    Promise<com.junbo.identity.spec.v1.model.Locale> get(LocaleId id) {
        return localeRepository.get(id)
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Locale> create(com.junbo.identity.spec.v1.model.Locale model) {
        return localeRepository.create(model)
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Locale> update(com.junbo.identity.spec.v1.model.Locale model, com.junbo.identity.spec.v1.model.Locale oldModel) {
        return localeRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        return localeRepository.delete(id)
    }

    @Override
    Promise<Results<com.junbo.identity.spec.v1.model.Locale>> searchAll(Integer limit, Integer offset) {
        return localeRepository.searchAll(limit, offset)
    }

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
    }
}
