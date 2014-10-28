package com.junbo.store.clientproxy.oauth.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.ViewModel
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.oauth.OAuthFacade
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.core.Response

/**
 * The OAuthFacadeImpl class.
 */
@CompileStatic
@Component('storeOAuthFacade')
class OAuthFacadeImpl implements OAuthFacade {

    @Value('${store.clientproxy.inprocess}')
    private boolean isInProc

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Override
    Promise<ViewModel> verifyEmail(String evc, String locale) {
        if (isInProc) {
            return resourceContainer.emailVerifyEndpointInProc.verifyEmail(evc, locale).then { Response response ->
                if (response?.entity instanceof ViewModel) {
                    return Promise.pure(response.entity)
                }
                return Promise.pure()
            }
        }
        return resourceContainer.emailVerifyEndpoint.verifyEmail(evc, locale)
    }

    @Override
    Promise sendVerifyEmail(String locale, String country, UserId userId, UserPersonalInfoId targetMail) {
        if (isInProc) {
            return resourceContainer.emailVerifyEndpointInProc.sendVerifyEmail(locale, country, userId, targetMail)
        }
        return resourceContainer.emailVerifyEndpoint.sendVerifyEmail(locale, country, userId, targetMail)
    }

    @Override
    Promise sendWelcomeEmail(String locale, String country, UserId userId) {
        if (isInProc) {
            return resourceContainer.emailVerifyEndpointInProc.sendWelcomeEmail(locale, country, userId)
        }
        return resourceContainer.emailVerifyEndpoint.sendWelcomeEmail(locale, country, userId)
    }
}
