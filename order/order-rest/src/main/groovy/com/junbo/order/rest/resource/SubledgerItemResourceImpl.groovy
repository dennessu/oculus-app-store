package com.junbo.order.rest.resource
import com.junbo.common.id.OrderItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.SubledgerService
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Created by fzhang on 4/2/2014.
 */
@CompileStatic
@TypeChecked
@Component('defaultSubledgerItemResource')
class SubledgerItemResourceImpl implements SubledgerItemResource {

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Override
    Promise<SubledgerItem> createSubledgerItem(SubledgerItem subledgerItem) {
        return Promise.pure(subledgerService.createSubledgerItem(subledgerItem))
    }

    @Override
    Promise<List<SubledgerItem>> getSubledgerItemsByOrderItemId(OrderItemId orderItemId) {
        return Promise.pure(subledgerService.getSubledgerItemsByOrderItemId(orderItemId))
    }

    @Override
    Promise<Void> aggregateSubledgerItem(List<SubledgerItem> subledgerItems) {
        subledgerService.aggregateSubledgerItem(subledgerItems)
        return Promise.pure()
    }
}
