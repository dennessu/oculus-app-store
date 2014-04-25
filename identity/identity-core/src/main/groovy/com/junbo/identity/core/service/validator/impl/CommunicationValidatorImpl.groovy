package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.CommunicationId
import com.junbo.identity.core.service.validator.CommunicationValidator
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CommunicationValidatorImpl implements CommunicationValidator {
    @Override
    Promise<Communication> validateForGet(CommunicationId communicationId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(CommunicationListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(Communication communication) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(CommunicationId communicationId, Communication communication, Communication oldCommunication) {
        return null
    }
}
