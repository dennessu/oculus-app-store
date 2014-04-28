package com.junbo.order.db.repo.impl

import com.junbo.order.db.entity.CommonDbEntityWithDate
import com.junbo.order.spec.model.BaseOrderResource
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
class Utils {

    static void fillDateInfo(BaseOrderResource baseModelWithDate, CommonDbEntityWithDate commonDbEntityWithDate) {
        baseModelWithDate.createdBy = commonDbEntityWithDate.createdBy
        baseModelWithDate.createdTime = commonDbEntityWithDate.createdTime
        baseModelWithDate.updatedBy = commonDbEntityWithDate.updatedBy
        baseModelWithDate.updatedTime = commonDbEntityWithDate.updatedTime
    }
}
