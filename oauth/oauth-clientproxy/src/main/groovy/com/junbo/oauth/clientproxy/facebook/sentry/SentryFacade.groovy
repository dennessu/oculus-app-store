package com.junbo.oauth.clientproxy.facebook.sentry

import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.sentry.SentryRequest
import com.junbo.store.spec.model.external.sentry.SentryResponse

/**
 * Created by liangfu on 9/24/14.
 */
public interface SentryFacade {
    Promise<SentryResponse> doSentryCheck(SentryRequest request)

    SentryRequest createSentryRequest(String category, Map<String, String> textJson)

    SentryRequest createSentryRequest(String category, Map<String, String> textJson, String targetId)
}
