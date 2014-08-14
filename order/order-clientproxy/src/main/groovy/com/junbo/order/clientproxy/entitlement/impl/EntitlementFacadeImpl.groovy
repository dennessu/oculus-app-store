package com.junbo.order.clientproxy.entitlement.impl

import com.junbo.catalog.spec.model.item.Item
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppError
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.entitlement.spec.resource.EntitlementResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.entitlement.EntitlementFacade
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by LinYi on 2014/8/14.
 */
@CompileStatic
@TypeChecked
@Component('orderEntitlementFacade')
class EntitlementFacadeImpl implements EntitlementFacade {
    @Resource(name = 'order.entitlementClient')
    EntitlementResource entitlementResource

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitlementFacadeImpl)

    @Override
    Promise<List<Entitlement>> getEntitlements(UserId userId, Offer offer) {
        def entitlementSearchParam = new EntitlementSearchParam(
                userId: userId,
                itemIds: offer.items.collect { Item item ->
                    new ItemId(item.id)
                } as Set
        )
        def pageMetadata = new PageMetadata(
                start: 0,
                count: 20
        )
        return entitlementResource.searchEntitlements(entitlementSearchParam, pageMetadata)
                .syncRecover { Throwable throwable ->
            LOGGER.error('EntitlementFacadeImpl_Get_Entitlements_error, userId: {}', userId?.value?.toString())
            throw convertError(throwable).exception()
        }.syncThen { Results<Entitlement> results ->
            return results == null ? Collections.emptyList() : results.items
        }
    }

    private AppError convertError(Throwable throwable) {
        if (throwable instanceof AppErrorException) {
            return AppErrors.INSTANCE.entitlementConnectionError()
        } else {
            return AppCommonErrors.INSTANCE.internalServerError(new Exception(throwable))
        }
    }
}
