package com.junbo.order.clientproxy
import com.junbo.order.clientproxy.catalog.CatalogFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource
/**
* Created by LinYi on 14-2-25.
*/
@CompileStatic
@TypeChecked
class CatalogFacadeTest extends BaseTest {
    @Resource(name = 'mockCatalogFacade')
    CatalogFacade catalogFacade

    @Test
    void testGetOffer() {
        def offer = catalogFacade.getOfferRevision(String.valueOf(new Random().nextLong()), new Date()).get()
        assert (offer != null)
    }
}
