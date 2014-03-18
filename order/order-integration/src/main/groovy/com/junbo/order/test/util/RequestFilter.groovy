package com.junbo.order.test.util

import com.ning.http.client.filter.FilterContext
import com.ning.http.client.filter.FilterException
import com.ning.http.client.filter.ResponseFilter
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-3-18.
 */
class RequestFilter implements ResponseFilter, com.ning.http.client.filter.RequestFilter {

    private static final LOGGER = LoggerFactory.getLogger(RequestFilter)

    @Override
    FilterContext filter(FilterContext ctx) throws FilterException {
        LOGGER.info('=======Request=======:\n{}', ctx.request.stringData)
        return ctx
    }
}
