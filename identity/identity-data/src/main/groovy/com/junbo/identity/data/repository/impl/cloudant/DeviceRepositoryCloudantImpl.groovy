package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.id.DeviceId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.DeviceRepository
import com.junbo.identity.spec.v1.model.Device
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-9.
 */
@CompileStatic
class DeviceRepositoryCloudantImpl extends CloudantClient<Device> implements DeviceRepository {

    @Override
    Promise<Device> get(DeviceId deviceId) {
        return cloudantGet(deviceId.toString())
    }

    @Override
    Promise<Device> create(Device device) {
        return cloudantPost(device)
    }

    @Override
    Promise<Device> update(Device device, Device oldDevice) {
        return cloudantPut(device, oldDevice)
    }

    @Override
    Promise<Void> delete(DeviceId id) {
        throw new IllegalStateException('delete device not support')
    }

    @Override
    Promise<Device> searchBySerialNumber(String externalRef) {
        return queryView('by_serial_number', externalRef).then { List<Device> list ->
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }
    }

    @Override
    Promise<Results<Device>> listAll(int limit, int offset) {
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset
        ))
    }
}
