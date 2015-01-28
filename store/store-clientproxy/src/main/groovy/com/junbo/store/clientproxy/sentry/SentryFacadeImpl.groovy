package com.junbo.store.clientproxy.sentry

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.external.sentry.SentryFieldConstant
import com.junbo.store.spec.model.external.sentry.SentryQueryParam
import com.junbo.store.spec.model.external.sentry.SentryRequest
import com.junbo.store.spec.model.external.sentry.SentryResponse
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by liangfu on 9/24/14.
 */
@CompileStatic
@Component('storeSentryFacade')
public class SentryFacadeImpl implements SentryFacade {

    @Value('${store.sentry.format}')
    private String sentryFormat

    @Value('${store.sentry.namespace}')
    private String sentryNamespace

    @Value('${store.sentry.enable}')
    private Boolean enableSentry

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Value('${store.sentry.access_token}')
    private String accessToken

    @Value('${store.sentry.method}')
    private String method

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

        return resourceContainer.sentryResource.doSentryCheck(queryParam).then { String response ->

            if (response == "[]") {
                return Promise.pure(new SentryResponse())
            } else {
                return Promise.pure(ObjectMapperProvider.instanceNotStrict().readValue(response, SentryResponse))
            }
        }
    }

    @Override
    SentryRequest createSentryRequest(String category, Map<String, String> textJson) {
        return new SentryRequest(
                category: category,
                textJson: ObjectMapperProvider.instanceNotStrict().writeValueAsString(textJson),
                otherJson: ObjectMapperProvider.instanceNotStrict().writeValueAsString(createOtherJsonMap())
        )
    }

    @Override
    SentryRequest createSentryRequest(String category, Map<String, String> textJson, String targetId) {
        return new SentryRequest(
                category: category,
                targetId: targetId,
                textJson: ObjectMapperProvider.instanceNotStrict().writeValueAsString(textJson),
                otherJson: ObjectMapperProvider.instanceNotStrict().writeValueAsString(createOtherJsonMap())
        )
    }

    private Map<String, String> createOtherJsonMap() {
        def otherMap = [:];
        def appJson = [:];
        appJson[SentryFieldConstant.ANDRIOD_ID.value] = JunboHttpContext.requestHeaders?.getFirst(StoreApiHeader.ANDROID_ID.value)
        otherMap[SentryFieldConstant.APP_JSON.value] = appJson
    }
}