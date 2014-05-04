package com.junbo.cart.test.util

import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 14-3-18.
 */
@CompileStatic
class AsyncHttpClientFactory {

    AsyncHttpClient createAsyncHttpClient() {
        def config = new AsyncHttpClientConfigBean()
        config.redirectEnabled = true
        config.addRequestFilter(new RequestFilter())
        config.addResponseFilters(new ResponseFilter())
        return new AsyncHttpClient(config)
    }
}
