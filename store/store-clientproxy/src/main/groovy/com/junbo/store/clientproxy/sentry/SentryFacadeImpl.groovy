package com.junbo.store.clientproxy.sentry

import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.model.external.sentry.SentryQueryParam
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

    @Value('${store.sentry.category}')
    private String sentryCategory

    @Value('${store.sentry.enable}')
    private Boolean enableSentry

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Override
    Promise<SentryResponse> doSentryCheck(String accessToken, String method, String sourceId, String targetId, String textJson, String otherJson) {
        if (!enableSentry) {
            return Promise.pure(null)
        }
        SentryQueryParam queryParam = new SentryQueryParam(
                textJson: textJson,
                otherJson: otherJson,
                ipAddress: JunboHttpContext.requestIpAddress, //user's ip address
                format: sentryFormat,
                method: method,
                accessToken: accessToken,
                namespace: sentryNamespace,
                category: sentryCategory,
                sourceId: sourceId,
                targetId: targetId
        )

        return resourceContainer.sentryResource.doSentryCheck(queryParam)
    }
}

