package com.junbo.oauth.clientproxy.facebook.sentry

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.external.sentry.SentryFieldConstant
import com.junbo.store.spec.model.external.sentry.SentryQueryParam
import com.junbo.store.spec.model.external.sentry.SentryRequest
import com.junbo.store.spec.model.external.sentry.SentryResponse
import com.junbo.store.spec.resource.external.SentryResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component

/**
 * Created by liangfu on 9/24/14.
 */
@CompileStatic
@Component('oauthSentryFacade')
public class SentryFacadeImpl implements SentryFacade {

    private String sentryFormat

    private String sentryNamespace

    private Boolean enableSentry

    private String accessToken

    private String method

    private SentryResource sentryResource

    @Override
    Promise<SentryResponse> doSentryCheck(SentryRequest request) {
        if (!enableSentry) {
            return Promise.pure(null)
        }
        SentryQueryParam queryParam = new SentryQueryParam(
                textJson: request.getTextJson(),
                otherJson: request.getOtherJson(),
                ipAddress: JunboHttpContext.requestIpAddress, //user's ip address
                format: sentryFormat,
                method: method,
                accessToken: accessToken,
                namespace: sentryNamespace,
                userAgent: JunboHttpContext.requestHeaders?.getFirst(StoreApiHeader.USER_AGENT.value),
                category: request.getCategory(),
                sourceId: AuthorizeContext.currentUserId?.toString(),
                targetId: request.getTargetId(),
                locale: JunboHttpContext.requestHeaders?.getFirst(StoreApiHeader.ACCEPT_LANGUAGE.value)
        )

        return sentryResource.doSentryCheck(queryParam).then { String response ->

            if (response == "[]") {
                return Promise.pure(new SentryResponse())
            } else {
                return Promise.pure(ObjectMapperProvider.instance().readValue(response, SentryResponse))
            }
        }
    }

    @Override
    SentryRequest createSentryRequest(String category, Map<String, String> textJson) {
        return new SentryRequest(
                category: category,
                textJson: ObjectMapperProvider.instance().writeValueAsString(textJson),
                otherJson: ObjectMapperProvider.instance().writeValueAsString(createOtherJsonMap())
        )
    }

    @Override
    SentryRequest createSentryRequest(String category, Map<String, String> textJson, String targetId) {
        return new SentryRequest(
                category: category,
                targetId: targetId,
                textJson: ObjectMapperProvider.instance().writeValueAsString(textJson),
                otherJson: ObjectMapperProvider.instance().writeValueAsString(createOtherJsonMap())
        )
    }

    private Map<String, String> createOtherJsonMap() {
        def otherMap = [:];
        def appJson = [:];
        appJson[SentryFieldConstant.ANDRIOD_ID.value] = JunboHttpContext.requestHeaders?.getFirst(StoreApiHeader.ANDROID_ID.value)
        otherMap[SentryFieldConstant.APP_JSON.value] = appJson
    }

    @Required
    void setSentryFormat(String sentryFormat) {
        this.sentryFormat = sentryFormat
    }

    @Required
    void setSentryNamespace(String sentryNamespace) {
        this.sentryNamespace = sentryNamespace
    }

    @Required
    void setEnableSentry(Boolean enableSentry) {
        this.enableSentry = enableSentry
    }

    @Required
    void setAccessToken(String accessToken) {
        this.accessToken = accessToken
    }

    @Required
    void setMethod(String method) {
        this.method = method
    }

    @Required
    void setSentryResource(SentryResource sentryResource) {
        this.sentryResource = sentryResource
    }
}