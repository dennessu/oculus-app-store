package com.junbo.order.core.impl.common

import com.junbo.common.error.AppCommonErrors
import com.junbo.order.spec.model.OrderQueryParam
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.order.spec.model.enums.PayoutStatus
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

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

    static SubledgerParam processSubledgerParam(SubledgerParam subledgerParam) {
        def param =  subledgerParam
        if (param == null) {
            param = new SubledgerParam()
        }

        if (StringUtils.isEmpty(param.payOutStatus)) {
            param.payOutStatus = PayoutStatus.PENDING.name()
        }

        if (param.sellerId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('sellerId').exception()
        }

        return param
    }
}
