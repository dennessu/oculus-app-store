package com.junbo.order.rest.resource
import com.junbo.common.id.SubledgerId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.SubledgerService
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.order.spec.resource.SubledgerResource
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 4/2/2014.
 */
@CompileStatic
@Scope('prototype')
@Component('defaultSubledgerResource')
class SubledgerResourceImpl implements SubledgerResource {

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Override
    Promise<Subledger> putSubledger(SubledgerId subledgerId, Subledger subledger) {
        subledger.subledgerId = subledgerId
        return Promise.pure(null)
    }

    @Override
    Promise<Subledger> getSubledger(SubledgerId subledgerId) {
        return Promise.pure(null)
    }

    @Override
    Promise<Results<Subledger>> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        return Promise.pure(null)
    }
}
