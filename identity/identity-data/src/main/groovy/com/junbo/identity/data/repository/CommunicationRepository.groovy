package com.junbo.identity.data.repository

import com.junbo.common.id.CommunicationId
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
interface CommunicationRepository extends IdentityBaseRepository<Communication, CommunicationId> {
    @ReadMethod
    Promise<Communication> searchByName(String name)
}
