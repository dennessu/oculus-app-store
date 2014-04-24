package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.CommunicationId
import com.junbo.identity.data.dao.CommunicationDAO
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationRepositorySqlImpl implements CommunicationRepository {
    @Override
    Promise<Communication> searchByName(String name) {
        return null
    }

    @Override
    Promise<Communication> create(Communication model) {
        return null
    }

    @Override
    Promise<Communication> update(Communication model) {
        return null
    }

    @Override
    Promise<Communication> get(CommunicationId id) {
        return null
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        return null
    }
}
