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
import com.junbo.order.clientproxy.model.ItemEntry
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.util.regex.Matcher
import java.util.regex.Pattern

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
                itemIds: offer.items.collect { ItemEntry itemEntry ->
                    new ItemId(itemEntry.item.getId())
                } as Set
        )
        def count = offer.items.size() * 3
        String href = null
        def entitlements = []
        def entitlementSearchResults = []
        while ('END' != href) {
            def pageMetadata = new PageMetadata(
                    count: count,
                    bookmark: getBookmark(href)
            )
            Results<Entitlement> results = entitlementResource.searchEntitlements(
                    entitlementSearchParam, pageMetadata).get()
            entitlementSearchResults = results == null ? Collections.emptyList() : results.items
            entitlements.addAll(entitlementSearchResults)
            href = results.next.href
        }

        return Promise.pure(entitlements)
    }

    private String getBookmark(String href) {
        if (href == null) {
            return null
        }

        Pattern p = Pattern.compile('.*bookmark=(.*)&count=\\d*')
        Matcher m = p.matcher(href)
        if (m.find()) {
            return m.group(1)
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
