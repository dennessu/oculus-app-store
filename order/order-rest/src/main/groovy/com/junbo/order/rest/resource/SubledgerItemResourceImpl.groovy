package com.junbo.order.rest.resource

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.SubledgerService
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 4/2/2014.
 */
@CompileStatic
@TypeChecked
@Scope('prototype')
@Component('defaultSubledgerItemResource')
class SubledgerItemResourceImpl implements SubledgerItemResource {

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Override
    Promise<SubledgerItem> createSubledgerItem(SubledgerItem subledgerItem) {
        return Promise.pure(subledgerService.createSubledgerItem(subledgerItem))
    }
}
