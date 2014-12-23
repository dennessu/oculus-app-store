package com.junbo.langur.core.client

/**
 * Created by kg on 5/17/2014.
 */
interface AccessTokenProvider {
    String getTokenType()
    String getAccessToken()
}
