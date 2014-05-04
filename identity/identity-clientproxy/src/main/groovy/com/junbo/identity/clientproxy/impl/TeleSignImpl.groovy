package com.junbo.identity.clientproxy.impl

import com.junbo.identity.clientproxy.TeleSign
import com.junbo.identity.data.identifiable.TeleVerifyType
import com.junbo.identity.spec.error.AppErrors
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

    TeleSignImpl(String customerId, String secretKey) {
        verify = new Verify(customerId, secretKey)
    }

    @Override
    Promise<VerifyResponse> verifyCode(UserTeleCode userTeleCode) {
        if (userTeleCode.verifyType == TeleVerifyType.SMS.toString()) {
            return sms(userTeleCode)
        }
        return call(userTeleCode)
    }

    private Promise<VerifyResponse> sms(UserTeleCode userTeleCode) {
        VerifyResponse response = null
        try {
            response = verify.sms(userTeleCode.phoneNumber, userTeleCode.sentLanguage, userTeleCode.verifyCode,
                    userTeleCode.template)
        } catch (Exception e) {
            throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
        }

        return Promise.pure(response)
    }

    private Promise<VerifyResponse> call(UserTeleCode userTeleCode) {
        VerifyResponse response = null
        try {
            response = verify.call(userTeleCode.phoneNumber, userTeleCode.sentLanguage)
        } catch (Exception e) {
            throw AppErrors.INSTANCE.teleSignProviderError(e.message).exception()
        }

        return Promise.pure(response)
    }
}
