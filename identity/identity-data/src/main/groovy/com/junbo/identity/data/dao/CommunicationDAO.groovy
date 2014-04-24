package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.CommunicationEntity
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
interface CommunicationDAO {
    CommunicationEntity get(Long communicationId)
    CommunicationEntity save(CommunicationEntity entity)
    CommunicationEntity update(CommunicationEntity entity)

    CommunicationEntity findIdByName(String name)
}
