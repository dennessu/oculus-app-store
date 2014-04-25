package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.CommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.identity.spec.v1.option.model.CommunicationGetOptions
import com.junbo.identity.spec.v1.resource.CommunicationResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/24/14.
 */
@Transactional
@CompileStatic
class CommunicationResourceImpl implements CommunicationResource {

    @Override
    Promise<Communication> create(Communication communication) {
        return null
    }

    @Override
    Promise<Communication> put(CommunicationId communicationId, Communication communication) {
        return null
    }

    @Override
    Promise<Communication> patch(CommunicationId communicationId, Communication communication) {
        return null
    }

    @Override
    Promise<Communication> get(CommunicationId communicationId, CommunicationGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<Communication>> list(CommunicationListOptions listOptions) {
        return null
    }
}
