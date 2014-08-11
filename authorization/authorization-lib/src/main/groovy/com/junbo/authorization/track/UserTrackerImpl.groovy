package com.junbo.authorization.track

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.id.UserId
import com.junbo.common.track.Tracker
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by xiali_000 on 2014/7/12.
 */
@CompileStatic
class UserTrackerImpl implements Tracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserTrackerImpl)

    @Override
    CloudantEntity trackCreate(CloudantEntity entity) {
        UserId userId = AuthorizeContext.currentUserId
        if (userId == null) {
            entity.createdBy = null
        } else {
            entity.createdBy = AuthorizeContext.currentUserId.value
        }
        entity.createdByClient = AuthorizeContext.currentClientId
        entity.createdTime = new Date()
        return entity
    }

    @Override
    CloudantEntity trackUpdate(CloudantEntity entity, CloudantEntity oldEntity) {
        UserId userId = AuthorizeContext.currentUserId
        if (userId == null) {
            entity.updatedBy = null
        } else {
            entity.updatedBy = AuthorizeContext.currentUserId.value
        }
        entity.createdBy = oldEntity.createdBy
        entity.createdByClient = oldEntity.createdByClient
        entity.createdTime = oldEntity.createdTime
        entity.updatedByClient = AuthorizeContext.currentClientId
        entity.updatedTime = new Date()
        return entity
    }
}
