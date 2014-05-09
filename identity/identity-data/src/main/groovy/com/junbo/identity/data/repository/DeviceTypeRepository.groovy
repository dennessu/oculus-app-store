package com.junbo.identity.data.repository

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod

/**
 * Created by haomin on 14-4-25.
 */
public interface DeviceTypeRepository extends IdentityBaseRepository<DeviceType, DeviceTypeId> {
    @ReadMethod
    Promise<List<DeviceType>> search(DeviceTypeListOptions options)
}