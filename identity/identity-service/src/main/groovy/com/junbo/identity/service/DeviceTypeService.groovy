package com.junbo.identity.service

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface DeviceTypeService {

    Promise<DeviceType> get(DeviceTypeId id)

    Promise<DeviceType> create(DeviceType model)

    Promise<DeviceType> update(DeviceType model, DeviceType oldModel)

    Promise<Void> delete(DeviceTypeId id)

    Promise<List<DeviceType>> searchAll(Integer limit, Integer offset)

    Promise<List<DeviceType>> searchByDeviceTypeCode(String typeCode, Integer limit, Integer offset)
}