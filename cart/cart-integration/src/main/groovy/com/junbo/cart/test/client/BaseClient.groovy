package com.junbo.cart.test.client

import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.langur.core.client.MessageTranscoder
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class BaseClient {

    protected AsyncHttpClient asyncHttpClient

    protected MessageTranscoder messageTranscoder = new JsonMessageTranscoder()

    protected String baseUrl

    BaseClient() {
        def configBean = new AsyncHttpClientConfigBean()
        configBean.redirectEnabled = true
        asyncHttpClient = new AsyncHttpClient(configBean)
    }

    void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl
    }
}
