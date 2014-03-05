package com.junbo.order.clientproxy

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import groovy.transform.CompileStatic
import org.testng.annotations.Test

import javax.annotation.Resource

/**
* Created by LinYi on 14-2-25.
*/
@CompileStatic
class CatalogFacadeTest extends BaseTest {
    @Resource(name = 'mockCatalogFacade')
    CatalogFacade catalogFacade

    @Test
    void testGetOffer() {
        def offerPromise = catalogFacade.getOffer(new Random().nextLong())

        offerPromise?.then(new Promise.Func<Offer, Promise>() {
            @Override
            Promise apply(Offer offer) {
                assert (offer != null)
            }
        } )
        assert (offerPromise != null)
    }
}
