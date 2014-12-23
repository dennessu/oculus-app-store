package com.junbo.identity.service.impl

import com.junbo.common.id.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.service.PITypeService
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class PITypeServiceImpl implements PITypeService {
    private PITypeRepository piTypeRepository

    @Override
    Promise<PIType> get(PITypeId id) {
        return piTypeRepository.get(id)
    }

    @Override
    Promise<PIType> create(PIType model) {
        return piTypeRepository.create(model)
    }

    @Override
    Promise<PIType> update(PIType model, PIType oldModel) {
        return piTypeRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        return piTypeRepository.delete(id)
    }

    @Override
    Promise<List<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset) {
        return piTypeRepository.searchByTypeCode(typeCode, limit, offset)
    }

    @Override
    Promise<List<PIType>> searchAll(Integer limit, Integer offset) {
        return piTypeRepository.searchAll(limit, offset)
    }

    @Required
    void setPiTypeRepository(PITypeRepository piTypeRepository) {
        this.piTypeRepository = piTypeRepository
    }
}
