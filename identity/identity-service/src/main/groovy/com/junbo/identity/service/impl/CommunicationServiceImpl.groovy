package com.junbo.identity.service.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.service.CommunicationService
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class CommunicationServiceImpl implements CommunicationService {
    private CommunicationRepository communicationRepository

    @Override
    Promise<Communication> get(CommunicationId id) {
        return communicationRepository.get(id)
    }

    @Override
    Promise<Communication> create(Communication model) {
        return communicationRepository.create(model)
    }

    @Override
    Promise<Communication> update(Communication model, Communication oldModel) {
        return communicationRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        return communicationRepository.delete(id)
    }

    @Override
    Promise<Results<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset) {
        return communicationRepository.searchByTranslation(translation, limit, offset)
    }

    @Override
    Promise<Results<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset) {
        return communicationRepository.searchByRegion(region, limit, offset)
    }

    @Override
    Promise<Results<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit, Integer offset) {
        return communicationRepository.searchByRegionAndTranslation(region, translation, limit, offset)
    }

    @Override
    Promise<Results<Communication>> searchAll(Integer limit, Integer offset) {
        return communicationRepository.searchAll(limit, offset)
    }

    @Required
    void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository
    }
}
