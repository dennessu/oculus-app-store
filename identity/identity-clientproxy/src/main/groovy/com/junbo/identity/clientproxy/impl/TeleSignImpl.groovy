package com.junbo.identity.clientproxy.impl

import com.junbo.identity.clientproxy.TeleSign
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.TFAVerifyType
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import com.telesign.verify.Verify
import com.telesign.verify.response.VerifyResponse
import groovy.transform.CompileStatic
import org.apache.commons.lang3.ArrayUtils
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 5/4/14.
 */

@CompileStatic
class TeleSignImpl implements TeleSign {
    private final Verify verify

    private final UserPersonalInfoRepository userPersonalInfoRepository
    private final LocaleRepository localeRepository

    TeleSignImpl(String customerId, String secretKey, UserPersonalInfoRepository userPersonalInfoRepository,
                 LocaleRepository localeRepository) {
        verify = new Verify(customerId, secretKey)
        this.userPersonalInfoRepository = userPersonalInfoRepository
        this.localeRepository = localeRepository
    }

    @Override
    Promise<VerifyResponse> verifyCode(UserTFA userTeleCode) {
        if (userTeleCode.verifyType == TFAVerifyType.SMS.toString()) {
            return sms(userTeleCode)
        }
        return call(userTeleCode)
    }

    private Promise<VerifyResponse> sms(UserTFA userTeleCode) {
        return userPersonalInfoRepository.get(userTeleCode.personalInfo).then { UserPersonalInfo userPersonalInfo ->
            PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value, PhoneNumber)

            return getLocaleText(userTeleCode).then { String language ->
                VerifyResponse response = null
                try {
                    response = verify.sms(phoneNumber.info, language, userTeleCode.verifyCode, userTeleCode.template)

                    if (ArrayUtils.isEmpty(response.errors)) {
                        return Promise.pure(response)
                    } else {
                        throw AppErrors.INSTANCE.teleSignProviderError("Invalid parameter to send sms").exception()
                    }

                    return Promise.pure(response)
                } catch (Exception e) {
                    throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
                }
            }
        }

    }

    private Promise<String> getLocaleText(UserTFA userTeleCode) {
        if (userTeleCode.sentLocale == null) {
            return Promise.pure(null)
        }
        return localeRepository.get(userTeleCode.sentLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
            String localeCode = locale.localeCode.replace('_', '-')
            return Promise.pure(localeCode)
        }
    }

    private Promise<VerifyResponse> call(UserTFA userTeleCode) {
        return userPersonalInfoRepository.get(userTeleCode.personalInfo).then { UserPersonalInfo userPersonalInfo ->
            PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value, PhoneNumber)

            return getLocaleText(userTeleCode).then { String language ->
                VerifyResponse response = null
                try {
                    response = verify.call(phoneNumber.info, language)

                    if (ArrayUtils.isEmpty(response.errors)) {
                        return Promise.pure(response)
                    } else {
                        throw AppErrors.INSTANCE.teleSignProviderError("Invalid parameter to call").exception()
                    }
                } catch (Exception e) {
                    throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
                }
            }
        }
    }
}
