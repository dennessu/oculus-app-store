package com.junbo.identity.data.repository

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.DeleteMethod
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.dualwrite.annotations.WriteMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by haomin on 14-4-25.
 */
public interface DeviceTypeRepository extends BaseRepository<DeviceType, DeviceTypeId> {

    Promise<DeviceType> get(DeviceTypeId id)

    Promise<DeviceType> create(DeviceType model)

    Promise<DeviceType> update(DeviceType model, DeviceType oldModel)

    Promise<Void> delete(DeviceTypeId id)

    Promise<List<DeviceType>> searchAll(Integer limit, Integer offset)

    @ReadMethod
    Promise<List<DeviceType>> searchByDeviceTypeCode(String typeCode, Integer limit, Integer offset)
}