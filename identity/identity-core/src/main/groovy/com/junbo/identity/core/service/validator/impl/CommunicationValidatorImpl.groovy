package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.CommunicationId
import com.junbo.identity.core.service.validator.CommunicationValidator
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CommunicationValidatorImpl implements CommunicationValidator {

    private CommunicationRepository communicationRepository

    @Required
    void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository
    }

    @Override
    Promise<Communication> validateForGet(CommunicationId communicationId) {
        if (communicationId == null || communicationId.value == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        return communicationRepository.get(communicationId).then { Communication communication ->
            if (communication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            return Promise.pure(communication)
        }
    }

    @Override
    Promise<Void> validateForSearch(CommunicationListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Communication communication) {
        checkBasicCommunicationInfo(communication)

        if (communication.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return communicationRepository.search(new CommunicationListOptions(name: communication.name)).then { Communication existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('name').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(CommunicationId communicationId, Communication communication, Communication oldCommunication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        if (communicationId != communication.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (communicationId != oldCommunication.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicCommunicationInfo(communication)

        if (communication.name != oldCommunication.name) {
            throw AppErrors.INSTANCE.fieldInvalid('name').exception()
        }

        return Promise.pure(null)
    }

    private void checkBasicCommunicationInfo(Communication communication) {
        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        if (communication.name == null) {
            throw AppErrors.INSTANCE.fieldRequired('name').exception()
        }
        if (communication.description == null) {
            throw AppErrors.INSTANCE.fieldRequired('description').exception()
        }
        if (communication.allowedIn == null) {
            throw AppErrors.INSTANCE.fieldRequired('allowedIn').exception()
        }
        if (communication.locales == null) {
            throw AppErrors.INSTANCE.fieldRequired('locales').exception()
        }
    }
}
