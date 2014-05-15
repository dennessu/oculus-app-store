package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.DeviceId
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-9.
 */
@CompileStatic
class DeviceRepositoryCloudantImpl extends CloudantClient<Device> implements DeviceRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    Promise<Device> get(DeviceId groupId) {
        return Promise.pure((Device)super.cloudantGet(groupId.toString()))
    }

    @Override
    Promise<Device> create(Device device) {
        if (device.id == null) {
            device.id = new DeviceId(idGenerator.nextId())
        }
        return Promise.pure((Device)super.cloudantPost(device))
    }

    @Override
    Promise<Device> update(Device device) {
        return Promise.pure((Device)super.cloudantPut(device))
    }

    @Override
    Promise<Void> delete(DeviceId id) {
        throw new IllegalStateException('delete device not support')
    }

    @Override
    Promise<Device> searchBySerialNumber(String externalRef) {
        def list = super.queryView('by_serial_number', externalRef)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_serial_number': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.serialNumber, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }
}
