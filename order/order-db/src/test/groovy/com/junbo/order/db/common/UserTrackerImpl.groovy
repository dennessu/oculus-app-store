package com.junbo.order.db.common

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.track.Tracker
import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/7/12.
 */
@CompileStatic
class UserTrackerImpl implements Tracker {
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
