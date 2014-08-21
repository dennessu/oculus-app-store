package com.junbo.store.rest.test.bvt

import com.junbo.entitlement.spec.resource.DownloadUrlResource
import com.junbo.store.rest.test.TestAccessTokenProvider
import com.junbo.store.rest.test.TestUtils
import com.junbo.store.spec.resource.LoginResource
import com.junbo.store.spec.resource.proxy.StoreResourceClientProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

/**
 * The TestBase class
 */
@Test
@ContextConfiguration(locations = ['classpath:spring/store-rest-test-context.xml'])
class TestBase extends AbstractTestNGSpringContextTests {

    public static String digitalOfferName = 'testOffer_CartCheckout_Digital1'

    public static String[] freeOfferNames = ['testOffer_Free_Digital', 'testOffer_FreeOculusAndroidDigital1', 'testOffer_FreeOculusAndroidDigital2']

    public static String physicalOfferName = 'testOffer_CartCheckout_Physical1'

    @Autowired(required = true)
    @Qualifier('storeClient')
    protected StoreResourceClientProxy storeResource

    @Autowired(required = true)
    @Qualifier('loginClient')
    protected LoginResource loginResource

    @Autowired(required = true)
    @Qualifier('downloadUrlClient')
    protected DownloadUrlResource downloadUrlResource

    @Autowired(required = true)
    @Qualifier('storeRestTestUtils')
    protected TestUtils testUtils

    @Autowired(required = true)
    @Qualifier('testAccessTokenProvider')
    protected TestAccessTokenProvider testAccessTokenProvider

}
