package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeRepositoryCloudantImpl extends CloudantClient<DeviceType> implements DeviceTypeRepository {

    @Override
    Promise<Results<DeviceType>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false, true)
    }

    @Override
    Promise<Results<DeviceType>> searchByDeviceTypeCode(String typeCode, Integer limit, Integer offset) {
        Results<DeviceType> results = new Results<>()
        return queryView('by_type_code', typeCode, limit, offset, false).then { List<DeviceType> deviceTypeList ->
            results.items = deviceTypeList

            return queryViewTotal('by_type_code', typeCode).then { Integer total ->
                results.total = total
                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<DeviceType> create(DeviceType deviceType) {
        if (deviceType.id == null) {
            deviceType.id = new DeviceTypeId(deviceType.typeCode)
        }

        return cloudantPost(deviceType)
    }

    @Override
    Promise<DeviceType> update(DeviceType deviceType, DeviceType oldDeviceType) {
        return cloudantPut(deviceType, oldDeviceType)
    }

    @Override
    Promise<DeviceType> get(DeviceTypeId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(DeviceTypeId id) {
        return cloudantDelete(id.toString())
    }
}
