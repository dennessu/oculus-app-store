package com.junbo.langur.core.async

import com.ning.http.client.AsyncHttpClientConfig
import com.ning.http.client.HttpResponseBodyPart
import com.ning.http.client.HttpResponseHeaders
import com.ning.http.client.HttpResponseStatus
import com.ning.http.client.Response
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider
import com.ning.http.client.providers.netty.NettyResponse
import groovy.transform.CompileStatic

/**
 * Created by contractor5 on 7/21/2014.
 */
@CompileStatic
class UTF8NettyAsyncHttpProvider extends NettyAsyncHttpProvider {
    UTF8NettyAsyncHttpProvider(AsyncHttpClientConfig config) {
        super(config)
    }

    @Override
    Response prepareResponse(HttpResponseStatus status, HttpResponseHeaders headers, List<HttpResponseBodyPart> bodyParts) {
        return new NettyResponse(status, headers, bodyParts) {
            @Override
            String getResponseBody() throws IOException {
                return super.getResponseBody('UTF-8')
            }
        }
    }
}
