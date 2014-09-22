package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Check phone format
 * Check phone minimum and maximum length
 * Check user's maximum phone number
 * Check maximum new phone numbers per month
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPhoneNumberValidatorImpl implements PiiValidator {
    private Integer minValueLength
    private Integer maxValueLength
    private List<Pattern> allowedValuePatterns

    private UserPersonalInfoRepository userPersonalInfoRepository
    private Integer maxUserNumberPerPhone
    private Integer maxNewPhoneNumberPerMonth
    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.PHONE.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(value, PhoneNumber)
        checkUserPhone(phoneNumber)
        if (userId != null) {
            return checkAdvanceUserPhone(phoneNumber, userId)
        } else {
            // todo:    Organization phone won't do any advanced check to user, do we need to change this?
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(value, PhoneNumber)
        PhoneNumber oldPhoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(oldValue, PhoneNumber)

        if (phoneNumber != oldPhoneNumber) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    private void checkUserPhone(PhoneNumber phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber.info)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value.info').exception()
        }

        if (phoneNumber.info.length() > maxValueLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxValueLength).exception()
        }
        if (phoneNumber.info.length() < minValueLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minValueLength).exception()
        }

        if (!allowedValuePatterns.any {
            Pattern pattern -> pattern.matcher(phoneNumber.info).matches()
        }) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value').exception()
        }
    }

    private Promise<Void> checkAdvanceUserPhone(PhoneNumber phoneNumber, UserId userId) {
        return userPersonalInfoRepository.searchByPhoneNumber(phoneNumber.info, null, maximumFetchSize, 0).then {
            List<UserPersonalInfo> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    // check this phone number is not used by this user
                    if (existing.any { UserPersonalInfo userPersonalInfo ->
                        return userPersonalInfo.userId == userId
                    }
                    ) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('value').exception()
                    }

                    // remove all phones not belonging to this user
                    existing.unique { UserPersonalInfo userPersonalInfo ->
                        return userPersonalInfo.userId
                    }.removeAll { UserPersonalInfo userPersonalInfo ->
                        userPersonalInfo.userId == userId
                    }

                    if (existing != null && existing.size() > maxUserNumberPerPhone) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'Reach maximum phoneNumber users')
                                .exception()
                    }
                }

                return userPersonalInfoRepository.searchByUserIdAndType(userId, UserPersonalInfoType.PHONE.toString(),
                        maximumFetchSize, 0).then { List<UserPersonalInfo> userPersonalInfoList ->

                    if (CollectionUtils.isEmpty(userPersonalInfoList)) {
                        return Promise.pure(null)
                    }

                    // Even the phone is updated, we will treat this as new added
                    userPersonalInfoList.sort( new Comparator<UserPersonalInfo>( ) {
                        @Override
                        int compare(UserPersonalInfo o1, UserPersonalInfo o2) {
                            Date o1LastChangedTime = o1.updatedTime == null ? o1.createdTime : o1.updatedTime
                            Date o2LastChangedTime = o2.updatedTime == null ? o2.createdTime : o2.updatedTime

                            return o2LastChangedTime <=> o1LastChangedTime
                        }
                    }
                    )

                    if (userPersonalInfoList.size() > maxNewPhoneNumberPerMonth) {
                        UserPersonalInfo userPersonalInfo = userPersonalInfoList.get(maxNewPhoneNumberPerMonth - 1)
                        Date lastChangedTime = userPersonalInfo.updatedTime == null ?
                                userPersonalInfo.createdTime : userPersonalInfo.updatedTime

                        Calendar calendar = Calendar.instance
                        calendar.setTime(new Date())
                        calendar.add(Calendar.MONTH, -1)
                        if (lastChangedTime.after(calendar.time)) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('value',
                                    "Reach maximum phoneNumber change numbers per month").exception()
                        }
                    }

                    return Promise.pure(null)
                }
        }
    }

    @Required
    void setAllowedValuePatterns(List<String> allowedValuePatterns) {
        this.allowedValuePatterns = allowedValuePatterns.collect {
            String allowedValuePattern -> Pattern.compile(allowedValuePattern)
        }
    }

    @Required
    void setMinValueLength(Integer minValueLength) {
        this.minValueLength = minValueLength
    }

    @Required
    void setMaxValueLength(Integer maxValueLength) {
        this.maxValueLength = maxValueLength
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Required
    void setMaxUserNumberPerPhone(Integer maxUserNumberPerPhone) {
        this.maxUserNumberPerPhone = maxUserNumberPerPhone
    }

    @Required
    void setMaxNewPhoneNumberPerMonth(Integer maxNewPhoneNumberPerMonth) {
        this.maxNewPhoneNumberPerMonth = maxNewPhoneNumberPerMonth
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }
}
