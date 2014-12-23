package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.DeviceTypeId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.core.service.validator.DeviceTypeValidator
import com.junbo.identity.service.DeviceTypeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.DeviceSoftware
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

    private DeviceTypeService deviceTypeService

    private List<String> allowedDeviceTypeCodeList

    private Integer minInstructionManualLength
    private Integer maxInstructionManualLength

    private Integer minAvailableSoftwareKeyLength
    private Integer maxAvailableSoftwareKeyLength

    private Integer minSoftwareObjectVersionLength
    private Integer maxSoftwareObjectVersionLength

    private Integer minSoftwareObjectHrefLength
    private Integer maxSoftwareObjectHrefLength

    @Required
    void setDeviceTypeService(DeviceTypeService deviceTypeService) {
        this.deviceTypeService = deviceTypeService
    }

    @Required
    void setAllowedDeviceTypeCodeList(List<String> allowedDeviceTypeCodeList) {
        this.allowedDeviceTypeCodeList = allowedDeviceTypeCodeList
    }

    @Required
    void setMinInstructionManualLength(Integer minInstructionManualLength) {
        this.minInstructionManualLength = minInstructionManualLength
    }

    @Required
    void setMaxInstructionManualLength(Integer maxInstructionManualLength) {
        this.maxInstructionManualLength = maxInstructionManualLength
    }

    @Required
    void setMinAvailableSoftwareKeyLength(Integer minAvailableSoftwareKeyLength) {
        this.minAvailableSoftwareKeyLength = minAvailableSoftwareKeyLength
    }

    @Required
    void setMaxAvailableSoftwareKeyLength(Integer maxAvailableSoftwareKeyLength) {
        this.maxAvailableSoftwareKeyLength = maxAvailableSoftwareKeyLength
    }

    @Required
    void setMinSoftwareObjectVersionLength(Integer minSoftwareObjectVersionLength) {
        this.minSoftwareObjectVersionLength = minSoftwareObjectVersionLength
    }

    @Required
    void setMaxSoftwareObjectVersionLength(Integer maxSoftwareObjectVersionLength) {
        this.maxSoftwareObjectVersionLength = maxSoftwareObjectVersionLength
    }

    @Required
    void setMinSoftwareObjectHrefLength(Integer minSoftwareObjectHrefLength) {
        this.minSoftwareObjectHrefLength = minSoftwareObjectHrefLength
    }

    @Required
    void setMaxSoftwareObjectHrefLength(Integer maxSoftwareObjectHrefLength) {
        this.maxSoftwareObjectHrefLength = maxSoftwareObjectHrefLength
    }

    @Override
    Promise<DeviceType> validateForGet(DeviceTypeId deviceTypeId) {
        if (deviceTypeId == null || deviceTypeId.value == null) {
            throw new IllegalArgumentException('deviceTypeId is null')
        }

        return deviceTypeService.get(deviceTypeId).then { DeviceType deviceType ->
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
            return deviceTypeService.searchByDeviceTypeCode(deviceType.typeCode, 1, 0).then {
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
                return deviceTypeService.searchByDeviceTypeCode(deviceType.typeCode, Integer.MAX_VALUE, 0).then {
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
                return deviceTypeService.get(iter).then { DeviceType existing ->
                    if (existing == null) {
                        throw AppErrors.INSTANCE.deviceTypeNotFound(iter).exception()
                    }

                    return Promise.pure(null)
                }
            }.then {
                return Promise.pure(null)
            }
        }

        if (deviceType.availableSoftware == null || deviceType.availableSoftware.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('availableSoftware').exception()
        }

        deviceType.availableSoftware.entrySet().each { Map.Entry<String, DeviceSoftware> entry ->
            String key = entry.key
            DeviceSoftware deviceSoftware = entry.value

            if (StringUtils.isEmpty(key)) {
                throw AppCommonErrors.INSTANCE.fieldRequired('availableSoftware.key').exception()
            }

            if (key.length() > maxAvailableSoftwareKeyLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('availableSoftware.key', maxAvailableSoftwareKeyLength).exception()
            }

            if (key.length() < minAvailableSoftwareKeyLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('availableSoftware.key', minAvailableSoftwareKeyLength).exception()
            }

            if (deviceSoftware == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('availableSoftware.value').exception()
            }

            if (deviceSoftware.dev != null) {
                if (deviceSoftware.dev.href != null) {
                    if (deviceSoftware.dev.href.length() > maxSoftwareObjectHrefLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooLong('availableSoftware.dev.href', maxSoftwareObjectHrefLength).exception()
                    }
                    if (deviceSoftware.dev.href.length() < minSoftwareObjectHrefLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooShort('availableSoftware.dev.href', minSoftwareObjectHrefLength).exception()
                    }
                }
                if (deviceSoftware.dev.version != null) {
                    if (deviceSoftware.dev.version.length() > maxSoftwareObjectVersionLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooLong('availableSoftware.dev.version', maxSoftwareObjectVersionLength).exception()
                    }
                    if (deviceSoftware.dev.version.length() < minSoftwareObjectVersionLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooShort('availableSoftware.dev.version', minSoftwareObjectVersionLength).exception()
                    }
                }
            }

            if (deviceSoftware.stable != null) {
                if (deviceSoftware.stable.href != null) {
                    if (deviceSoftware.stable.href.length() > maxSoftwareObjectHrefLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooLong('availableSoftware.stable.href', maxSoftwareObjectHrefLength).exception()
                    }
                    if (deviceSoftware.stable.href.length() < minSoftwareObjectHrefLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooShort('availableSoftware.dev.href', minSoftwareObjectHrefLength).exception()
                    }
                }
                if (deviceSoftware.stable.version != null) {
                    if (deviceSoftware.stable.version.length() > maxSoftwareObjectVersionLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooLong('availableSoftware.dev.version', maxSoftwareObjectVersionLength).exception()
                    }
                    if (deviceSoftware.stable.version.length() < minSoftwareObjectVersionLength) {
                        throw AppCommonErrors.INSTANCE.fieldTooShort('availableSoftware.dev.version', minSoftwareObjectVersionLength).exception()
                    }
                }
            }
        }

        return Promise.pure(null)
    }
}
