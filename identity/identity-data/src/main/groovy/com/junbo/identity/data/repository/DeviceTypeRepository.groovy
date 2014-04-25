package com.junbo.identity.data.repository

import com.junbo.common.id.DeviceTypeId
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-4-25.
 */
public interface DeviceTypeRepository extends IdentityBaseRepository<DeviceType, DeviceTypeId> {
    Promise<List<DeviceType>> search(DeviceTypeListOptions options)
}