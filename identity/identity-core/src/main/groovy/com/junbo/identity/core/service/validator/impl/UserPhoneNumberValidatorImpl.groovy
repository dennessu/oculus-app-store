package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
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

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.PHONE.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validate(JsonNode value, UserId userId) {
        PhoneNumber phoneNumber = ObjectMapperProvider.instance().treeToValue(value, PhoneNumber)
        if (phoneNumber.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (phoneNumber.value.length() > maxValueLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', maxValueLength).exception()
        }
        if (phoneNumber.value.length() < minValueLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', minValueLength).exception()
        }

        if (!allowedValuePatterns.any {
            Pattern pattern -> pattern.matcher(phoneNumber.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        return userPersonalInfoRepository.searchByPhoneNumber(phoneNumber.value).then {
            List<UserPersonalInfo> existing ->
                if (existing != null) {
                    existing.unique { UserPersonalInfo userPersonalInfo ->
                        return userPersonalInfo.userId
                    }.removeAll { UserPersonalInfo userPersonalInfo ->
                        userPersonalInfo.userId == userId
                    }

                    if (existing != null && existing.size() > maxUserNumberPerPhone) {
                        throw AppErrors.INSTANCE.fieldInvalidException('value', 'Reach maximum phoneNumber users')
                            .exception()
                    }
                }

                return userPersonalInfoRepository.searchByUserIdAndType(userId, UserPersonalInfoType.PHONE.toString())
                        .then { List<UserPersonalInfo> userPersonalInfoList ->
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
                            throw AppErrors.INSTANCE.fieldInvalidException('value',
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
}
