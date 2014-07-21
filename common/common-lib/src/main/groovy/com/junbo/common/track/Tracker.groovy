package com.junbo.common.track

import com.junbo.common.cloudant.CloudantEntity

/**
 * Created by xiali_000 on 2014/7/12.
 */
public interface Tracker <T extends CloudantEntity> {
    T trackCreate(T entity)

    T trackUpdate(T entity, T oldEntity)
}