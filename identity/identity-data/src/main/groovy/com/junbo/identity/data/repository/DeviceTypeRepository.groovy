package com.junbo.identity.data.repository

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by haomin on 14-4-25.
 */
public interface DeviceTypeRepository extends BaseRepository<DeviceType, DeviceTypeId> {
    @ReadMethod
    Promise<Results<DeviceType>> searchAll(Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<DeviceType>> searchByDeviceTypeCode(String typeCode, Integer limit, Integer offset)
}