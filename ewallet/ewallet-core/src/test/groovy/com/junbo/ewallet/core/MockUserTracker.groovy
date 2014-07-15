package com.junbo.ewallet.core

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.track.Tracker
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/7/12.
 */
@CompileStatic
class MockUserTracker implements Tracker {
    @Override
    CloudantEntity trackCreate(CloudantEntity entity) {
        entity.createdBy = 123L
        entity.createdTime = new Date()
        return entity
    }

    @Override
    CloudantEntity trackUpdate(CloudantEntity entity) {
        entity.updatedBy = 123L
        entity.updatedTime = new Date()
        return entity
    }
}
