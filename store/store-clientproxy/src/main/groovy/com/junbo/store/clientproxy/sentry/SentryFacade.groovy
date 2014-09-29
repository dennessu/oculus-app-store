package com.junbo.store.clientproxy.sentry

import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.external.sentry.SentryResponse

/**
 * Created by liangfu on 9/24/14.
 */
public interface SentryFacade {
    Promise<SentryResponse> doSentryCheck(String accessToken, String method, String sourceId, String targetId,
                                          String textJson, String otherJson)
}
