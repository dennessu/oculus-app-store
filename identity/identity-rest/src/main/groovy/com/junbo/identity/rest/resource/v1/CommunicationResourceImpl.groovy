package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.CommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.CommunicationFilter
import com.junbo.identity.core.service.validator.CommunicationValidator
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.identity.spec.v1.option.model.CommunicationGetOptions
import com.junbo.identity.spec.v1.resource.CommunicationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@Transactional
@CompileStatic
class CommunicationResourceImpl implements CommunicationResource {
    @Autowired
    private CommunicationRepository communicationRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private CommunicationFilter communicationFilter

    @Autowired
    private CommunicationValidator communicationValidator

    @Override
    Promise<Communication> create(Communication communication) {
        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        communication = communicationFilter.filterForCreate(communication)

        return communicationValidator.validateForCreate(communication).then {
            return communicationRepository.create(communication).then { Communication newCommunication ->
                created201Marker.mark(newCommunication.id)

                newCommunication = communicationFilter.filterForGet(newCommunication, null)
                return Promise.pure(newCommunication)
            }
        }
    }

    @Override
    Promise<Communication> put(CommunicationId communicationId, Communication communication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        return communicationRepository.get(communicationId).then { Communication oldCommunication ->
            if (oldCommunication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            communication = communicationFilter.filterForPut(communication, oldCommunication)

            return communicationValidator.validateForUpdate(communicationId, communication, oldCommunication).then {
                return communicationRepository.update(communication).then { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)
                    return Promise.pure(newCommunication)
                }
            }
        }
    }

    @Override
    Promise<Communication> patch(CommunicationId communicationId, Communication communication) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (communication == null) {
            throw new IllegalArgumentException('communication is null')
        }

        return communicationRepository.get(communicationId).then { Communication oldCommunication ->
            if (oldCommunication == null) {
                throw AppErrors.INSTANCE.communicationNotFound(communicationId).exception()
            }

            communication = communicationFilter.filterForPatch(communication, oldCommunication)

            return communicationValidator.validateForUpdate(communicationId, communication, oldCommunication).then {
                return communicationRepository.update(communication).then { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)
                    return Promise.pure(newCommunication)
                }
            }
        }
    }

    @Override
    Promise<Communication> get(CommunicationId communicationId, CommunicationGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return communicationValidator.validateForGet(communicationId).then { Communication communication ->
            communication = communicationFilter.filterForGet(communication, null)
            return Promise.pure(communication)
        }
    }

    @Override
    Promise<Results<Communication>> list(CommunicationListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return communicationValidator.validateForSearch(listOptions).then {
            return communicationRepository.search(listOptions).then { List<Communication> communicationList ->
                def result = new Results<Communication>(items: [])

                communicationList.each { Communication newCommunication ->
                    newCommunication = communicationFilter.filterForGet(newCommunication, null)

                    if (newCommunication != null) {
                        result.items.add(newCommunication)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Void> delete(CommunicationId communicationId) {
        if (communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        return communicationValidator.validateForGet(communicationId).then { Communication communication ->
            return communicationRepository.delete(communicationId)
        }
    }
}
