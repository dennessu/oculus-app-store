package com.junbo.identity.service.impl

import com.junbo.common.id.ErrorIdentifier
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.ErrorInfoRepository
import com.junbo.identity.service.ErrorInfoService
import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class ErrorInfoServiceImpl implements ErrorInfoService {
    private ErrorInfoRepository errorInfoRepository

    @Override
    Promise<ErrorInfo> get(ErrorIdentifier id) {
        return errorInfoRepository.get(id)
    }

    @Override
    Promise<ErrorInfo> create(ErrorInfo model) {
        return errorInfoRepository.create(model)
    }

    @Override
    Promise<ErrorInfo> update(ErrorInfo model, ErrorInfo oldModel) {
        return errorInfoRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(ErrorIdentifier id) {
        return errorInfoRepository.delete(id)
    }

    @Override
    Promise<Results<ErrorInfo>> searchAll(Integer limit, Integer offset) {
        return errorInfoRepository.searchAll(limit, offset)
    }

    @Required
    void setErrorInfoRepository(ErrorInfoRepository errorInfoRepository) {
        this.errorInfoRepository = errorInfoRepository
    }
}
