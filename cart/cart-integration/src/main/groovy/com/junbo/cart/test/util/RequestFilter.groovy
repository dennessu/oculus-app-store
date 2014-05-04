package com.junbo.cart.test.util

import com.ning.http.client.filter.FilterContext
import com.ning.http.client.filter.FilterException
import com.ning.http.client.filter.ResponseFilter
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-3-18.
 */
@CompileStatic
class RequestFilter implements ResponseFilter, com.ning.http.client.filter.RequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter)

    @Override
    FilterContext filter(FilterContext ctx) throws FilterException {
        LOGGER.info('=======Request=======:\n{}', ctx.request.stringData)
        return ctx
    }
}
