package com.junbo.identity.clientproxy.impl

import com.junbo.identity.clientproxy.TeleSign
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.identifiable.TeleVerifyType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PhoneNumber
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.langur.core.promise.Promise
import com.telesign.verify.Verify
import com.telesign.verify.response.VerifyResponse
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/4/14.
 */

@CompileStatic
class TeleSignImpl implements TeleSign {
    private final Verify verify

    private final UserPersonalInfoRepository userPersonalInfoRepository

    TeleSignImpl(String customerId, String secretKey, UserPersonalInfoRepository userPersonalInfoRepository) {
        verify = new Verify(customerId, secretKey)
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }

    @Override
    Promise<VerifyResponse> verifyCode(UserTeleCode userTeleCode) {
        if (userTeleCode.verifyType == TeleVerifyType.SMS.toString()) {
            return sms(userTeleCode)
        }
        return call(userTeleCode)
    }

    private Promise<VerifyResponse> sms(UserTeleCode userTeleCode) {
        return userPersonalInfoRepository.get(userTeleCode.phoneNumber).then { UserPersonalInfo userPersonalInfo ->

            PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value, PhoneNumber)

            VerifyResponse response = null
            try {
                // todo:    Get locale language according to id. Hard-code it here.
                response = verify.sms(phoneNumber.info, "en_US", userTeleCode.verifyCode,
                        userTeleCode.template)
            } catch (Exception e) {
                throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
            }

            return Promise.pure(response)
        }

    }

    private Promise<VerifyResponse> call(UserTeleCode userTeleCode) {
        return userPersonalInfoRepository.get(userTeleCode.phoneNumber).then { UserPersonalInfo userPersonalInfo ->
            PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(userPersonalInfo.value, PhoneNumber)
            VerifyResponse response = null
            try {
                // todo:    Get locale language according to id. Hard-code it here.
                response = verify.call(phoneNumber.info, "en_US")
            } catch (Exception e) {
                throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
            }

            return Promise.pure(response)
        }
    }
}
