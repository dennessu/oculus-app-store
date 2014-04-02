package com.junbo.order.core.impl.common

import com.junbo.order.spec.model.OrderQueryParam
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
/**
 * Created by fzhang on 4/1/2014.
 */
@CompileStatic
class ParamUtils {

    public static final int DEFAULT_COUNT = 50

    static PageParam processPageParam(PageParam pageParam) {
        def param =  pageParam
        if (param == null) {
            param = new PageParam()
        }

        if (param.start == null || param.start < 0) {
            param.start = 0
        }
        if (param.count == null || param.count <= 0) {
            param.count = DEFAULT_COUNT
        }

        return param
    }

    static OrderQueryParam processOrderQueryParam(OrderQueryParam orderQueryParam) {
        def param =  orderQueryParam
        if (param == null) {
            param = new OrderQueryParam()
        }
        return param
    }
}
