package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.data.repository.DeviceTypeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.DeviceType
import com.junbo.identity.spec.v1.option.list.DeviceTypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class DeviceTypeValidatorImpl implements DeviceTypeValidator {

    private DeviceTypeRepository deviceTypeRepository

    private List<String> allowedDeviceTypeCodeList

    private Integer minAvailableFirmwaresKeyLength
    private Integer maxAvailableFirmwaresKeyLength

    private Integer minAvailableFirmwaresValueLength
    private Integer maxAvailableFirmwaresValueLength

    private Integer minInstructionManualLength
    private Integer maxInstructionManualLength

    @Required
    void setDeviceTypeRepository(DeviceTypeRepository deviceTypeRepository) {
        this.deviceTypeRepository = deviceTypeRepository
    }

    @Required
    void setAllowedDeviceTypeCodeList(List<String> allowedDeviceTypeCodeList) {
        this.allowedDeviceTypeCodeList = allowedDeviceTypeCodeList
    }

    @Required
    void setMinAvailableFirmwaresKeyLength(Integer minAvailableFirmwaresKeyLength) {
        this.minAvailableFirmwaresKeyLength = minAvailableFirmwaresKeyLength
    }

    @Required
    void setMaxAvailableFirmwaresKeyLength(Integer maxAvailableFirmwaresKeyLength) {
        this.maxAvailableFirmwaresKeyLength = maxAvailableFirmwaresKeyLength
    }

    @Required
    void setMinAvailableFirmwaresValueLength(Integer minAvailableFirmwaresValueLength) {
        this.minAvailableFirmwaresValueLength = minAvailableFirmwaresValueLength
    }

    @Required
    void setMaxAvailableFirmwaresValueLength(Integer maxAvailableFirmwaresValueLength) {
        this.maxAvailableFirmwaresValueLength = maxAvailableFirmwaresValueLength
    }

    @Required
    void setMinInstructionManualLength(Integer minInstructionManualLength) {
        this.minInstructionManualLength = minInstructionManualLength
    }

    @Required
    void setMaxInstructionManualLength(Integer maxInstructionManualLength) {
        this.maxInstructionManualLength = maxInstructionManualLength
    }

    @Override
    Promise<DeviceType> validateForGet(DeviceTypeId deviceTypeId) {
        if (deviceTypeId == null || deviceTypeId.value == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        return deviceTypeRepository.get(deviceTypeId).then { DeviceType deviceType ->
            if (deviceType == null) {
                throw AppErrors.INSTANCE.deviceTypeNotFound(deviceTypeId).exception()
            }

            return Promise.pure(deviceType)
        }
    }

    @Override
    Promise<Void> validateForSearch(DeviceTypeListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(DeviceType deviceType) {
        if (deviceType.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return checkBasicDeviceType(deviceType).then {
            return deviceTypeRepository.searchByDeviceTypeCode(deviceType.typeCode, Integer.MAX_VALUE, 0).then {
                List<DeviceType> deviceTypeList ->
                    if (!CollectionUtils.isEmpty(deviceTypeList)) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('typeCode').exception()
                    }

                    return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(DeviceTypeId deviceTypeId, DeviceType deviceType, DeviceType oldDeviceType) {
        if (deviceTypeId == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        if (deviceType == null) {
            throw new IllegalArgumentException('deviceType is null')
        }

        if (deviceTypeId != deviceType.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (deviceTypeId != oldDeviceType.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        return checkBasicDeviceType(deviceType).then {
            if (deviceType.typeCode != oldDeviceType.typeCode) {
                return deviceTypeRepository.searchByDeviceTypeCode(deviceType.typeCode, Integer.MAX_VALUE, 0).then {
                    List<DeviceType> deviceTypeList ->
                        if (!CollectionUtils.isEmpty(deviceTypeList)) {
                            throw AppCommonErrors.INSTANCE.fieldDuplicate('typeCode').exception()
                        }

                        return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    Promise<Void> checkBasicDeviceType(DeviceType deviceType) {
        if (deviceType.typeCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('typeCode').exception()
        }
        if (!(deviceType.typeCode in allowedDeviceTypeCodeList)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('typeCode', allowedDeviceTypeCodeList.join(',')).exception()
        }

        if (deviceType.availableFirmwares == null || deviceType.availableFirmwares.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('availableFirmwares').exception()
        }
        deviceType.availableFirmwares.each { Map.Entry<String, String> pair ->
            String key = pair.key
            String value = pair.value

            if (StringUtils.isEmpty(key)) {
                throw AppCommonErrors.INSTANCE.fieldRequired('availableFirmwares.key').exception()
            }
            if (key.length() < minAvailableFirmwaresKeyLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('availableFirmwares.key',
                        minAvailableFirmwaresKeyLength).exception()
            }
            if (key.length() > maxAvailableFirmwaresKeyLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('availableFirmwares.key',
                        maxAvailableFirmwaresKeyLength).exception()
            }

            if (StringUtils.isEmpty(value)) {
                throw AppCommonErrors.INSTANCE.fieldRequired('availableFirmwares.value').exception()
            }
            if (value.length() < minAvailableFirmwaresValueLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('availableFirmwares.value',
                        minAvailableFirmwaresValueLength).exception()
            }
            if (value.length() > maxAvailableFirmwaresValueLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('availableFirmwares.value',
                        maxAvailableFirmwaresValueLength).exception()
            }
        }

        if (deviceType.instructionManual == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('instructionManual').exception()
        }
        if (deviceType.instructionManual.length() < minInstructionManualLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('instructionManual', minInstructionManualLength).exception()
        }
        if (deviceType.instructionManual.length() > maxInstructionManualLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('instructionManual', maxInstructionManualLength).exception()
        }

        if (!CollectionUtils.isEmpty(deviceType.componentTypes)) {
            return Promise.each(deviceType.componentTypes) { DeviceTypeId iter ->
                return deviceTypeRepository.get(iter).then { DeviceType existing ->
                    if (existing == null) {
                        throw AppErrors.INSTANCE.deviceTypeNotFound(iter).exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }
}
