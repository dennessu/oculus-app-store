package com.junbo.store.clientproxy

import com.junbo.store.clientproxy.casey.CaseyFacade
import com.junbo.store.clientproxy.catalog.CatalogFacade
import com.junbo.store.clientproxy.entitlement.EntitlementFacade
import com.junbo.store.clientproxy.oauth.OAuthFacade
import com.junbo.store.clientproxy.order.OrderFacade
import com.junbo.store.clientproxy.rating.PriceRatingFacade
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The FacadeContainer class.
 */
@CompileStatic
@Component('storeFacadeContainer')
class FacadeContainer {

    @Resource(name = 'storeCatalogFacade')
    CatalogFacade catalogFacade

    @Resource(name = 'storeCaseyFacade')
    CaseyFacade caseyFacade

    @Resource(name = 'storePriceRatingFacade')
    PriceRatingFacade priceRatingFacade

    @Resource(name = 'storeOrderFacade')
    OrderFacade orderFacade

    @Resource(name = 'storeEntitlementFacade')
    EntitlementFacade entitlementFacade

    @Resource(name = 'storeOAuthFacade')
    OAuthFacade oAuthFacade
}
