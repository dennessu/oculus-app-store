package com.junbo.cart.test.util

import com.ning.http.client.AsyncHandler
import com.ning.http.client.HttpResponseBodyPart
import com.ning.http.client.HttpResponseHeaders
import com.ning.http.client.HttpResponseStatus
import com.ning.http.client.filter.FilterContext
import com.ning.http.client.filter.FilterException
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-3-18.
 */
class ResponseFilter implements com.ning.http.client.filter.ResponseFilter {

    private static final LOGGER  = LoggerFactory.getLogger(ResponseFilter)

    private class ResponseHandler implements AsyncHandler {

        AsyncHandler wrapped

        List<Byte> body = new ArrayList<>()

        ResponseHandler(AsyncHandler wrapped) {
            this.wrapped = wrapped
        }

        @Override
        void onThrowable(Throwable t) {
            wrapped.onThrowable(t)
        }

        @Override
        AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
            if (bodyPart.bodyPartBytes != null) {
                bodyPart.bodyPartBytes.each {
                    body.add(it)
                }
            }

            if (bodyPart.last) {
                byte[] bytes = new byte[body.size()]
                for (int i = 0; i < bytes.length; ++i) {
                    bytes[i] = body[i]
                }
                LOGGER.info('=======Response=======:\n{}', new String(bytes), 'UTF-8')
            }
            wrapped.onBodyPartReceived(bodyPart)
        }

        @Override
        AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
            wrapped.onStatusReceived(responseStatus)
        }

        @Override
        AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
            wrapped.onHeadersReceived(headers)
        }

        @Override
        Object onCompleted() throws Exception {
            wrapped.onCompleted()
        }
    }

    @Override
    FilterContext filter(FilterContext ctx) throws FilterException {
        def builder = new FilterContext.FilterContextBuilder(ctx)
        builder.asyncHandler(new ResponseHandler(ctx.asyncHandler))
        return builder.build()
    }


}
