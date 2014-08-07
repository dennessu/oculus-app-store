package com.junbo.store.rest.test

import com.junbo.langur.core.client.AccessTokenProvider

/**
 * The AccessTokenHeaderProvider class.
 */
class TestAccessTokenProvider implements AccessTokenProvider {

    String token

    @Override
    String getAccessToken() {
        return token
    }
}
