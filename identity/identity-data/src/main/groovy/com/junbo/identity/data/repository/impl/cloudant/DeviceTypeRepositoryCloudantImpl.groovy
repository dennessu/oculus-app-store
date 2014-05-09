package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.DeviceTypeId
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeRepositoryCloudantImpl extends CloudantClient<DeviceType> implements DeviceTypeRepository {
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<List<DeviceType>> search(DeviceTypeListOptions options) {
        return Promise.pure(super.cloudantGetAll())
    }

    @Override
    Promise<DeviceType> create(DeviceType deviceType) {
        if (deviceType.id == null) {
            // hard code to shard 0 for all device type
            deviceType.id = new DeviceTypeId(deviceType.typeCode)
        }

        return Promise.pure((DeviceType)super.cloudantPost(deviceType))
    }

    @Override
    Promise<DeviceType> update(DeviceType deviceType) {
        return Promise.pure((DeviceType)super.cloudantPut(deviceType))
    }

    @Override
    Promise<DeviceType> get(DeviceTypeId id) {
        return Promise.pure((DeviceType)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(DeviceTypeId id) {
        super.cloudantDelete(id.value.toString())
        return Promise.pure(null)
    }
}
