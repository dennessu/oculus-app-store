package com.junbo.identity.core.service.validator

import com.junbo.common.id.CommunicationId
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
public interface CommunicationValidator {
    Promise<Communication> validateForGet(CommunicationId communicationId)
    Promise<Void> validateForSearch(CommunicationListOptions options)
    Promise<Void> validateForCreate(Communication communication)
    Promise<Void> validateForUpdate(CommunicationId communicationId, Communication communication, Communication oldCommunication)
}