package com.junbo.store.rest.test.bvt
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.OfferId
import com.junbo.store.rest.test.Generator
import com.junbo.store.rest.test.TestUtils
import com.junbo.store.spec.model.EntitlementsGetRequest
import com.junbo.store.spec.model.PageParam
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest
import com.junbo.store.spec.model.identity.UserProfileGetRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.LoginResource
import com.junbo.store.spec.resource.StoreResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
/**
 * The StoreApiTest class.
 */
@Test
@ContextConfiguration(locations = ['classpath:spring/store-rest-test-context.xml'])
class StoreApiTest extends AbstractTestNGSpringContextTests {

    public static String freeOfferName = 'testOffer_Free_Digital'

    public static String digitalOfferName = 'testOffer_CartCheckout_Digital1'

    public static String packageName = 'com.oculusvr.store.iap.sample'

    @Autowired(required = true)
    @Qualifier('storeResourceClientProxy')
    private StoreResource storeResource

    @Autowired(required = true)
    @Qualifier('loginResourceClientProxy')
    private LoginResource loginResource

    @Autowired(required = true)
    @Qualifier('storeRestTestUtils')
    private TestUtils testUtils

    @Test
    public void testUserProfile() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        def userId = result.userId

        result = storeResource.getUserProfile(new UserProfileGetRequest(userId: userId)).get()
        assert result.userProfile.userId == userId
        assert result.userProfile.emails.size() == 1
        assert result.userProfile.emails[0].type == 'EMAIL'

        storeResource.updateUserProfile(new UserProfileUpdateRequest())
    }

    @Test
    public void testAppPurchase() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        def userId = result.userId

        Instrument instrument = Generator.generateCreditCardInstrument()

        def billingProfile = storeResource.updateBillingProfile(new BillingProfileUpdateRequest(
                userId: userId, locale: new LocaleId('en_US'),
                action: BillingProfileUpdateRequest.UpdateAction.ADD_PI.name(), instrument: instrument),
                ).get().getBillingProfile()

        assert billingProfile.instruments.size() == 1

        OfferId offerId = testUtils.getByName('testOffer_CartCheckout_Digital1')
        result = storeResource.preparePurchase(new PreparePurchaseRequest(userId: userId, offerId: offerId, country: new CountryId('US'), locale: new LocaleId('en_US'), currency: new CurrencyId('USD'))).get()
        assert result.purchaseToken != null

        storeResource.selectInstrumentForPurchase(new SelectInstrumentRequest(purchaseToken: result.purchaseToken, userId: userId, instrumentId: billingProfile.instruments[0].instrumentId)).get()
        result = storeResource.commitPurchase(new CommitPurchaseRequest(userId: userId, purchaseToken: result.purchaseToken)).get()
        assert result.entitlements.size() == 2

        def download = result.entitlements.find {
            return it.type == 'DOWNLOAD'
        }
        assert download.appDeliveryData.downloadUrl != null
        assert download.appDeliveryData.downloadSize != null

        def run = result.entitlements.find {
            return it.type == 'RUN'
        }
        assert run.appDeliveryData == null

        result = storeResource.getEntitlements(new EntitlementsGetRequest(userId: userId), new PageParam()).get()
        assert result.entitlements.items.size() == 2
    }

    @Test
    public void testFreePurchase() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        def userId = result.userId

        OfferId offerId = testUtils.getByName(freeOfferName)
        result = storeResource.makeFreePurchase(new MakeFreePurchaseRequest(userId: userId, offerId: offerId, country: new CountryId('US'), locale: new LocaleId('en_US'))).get()
        assert result.status == 'SUCCESS'
        assert result.entitlements.size() == 2
        assert result.orderId != null

        def download = result.entitlements.find {
            return it.type == 'DOWNLOAD'
        }
        assert download.appDeliveryData.downloadUrl != null
        assert download.appDeliveryData.downloadSize != null

        def run = result.entitlements.find {
            return it.type == 'RUN'
        }
        assert run.appDeliveryData == null
    }

    @Test
    public void testInAppPurchase() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        def userId = result.userId

        Instrument instrument = Generator.generateCreditCardInstrument()

        def billingProfile = storeResource.updateBillingProfile(new BillingProfileUpdateRequest(
                userId: userId, locale: new LocaleId('en_US'),
                action: BillingProfileUpdateRequest.UpdateAction.ADD_PI.name(), instrument: instrument),
        ).get().getBillingProfile()

        assert billingProfile.instruments.size() == 1

        // todo use iapGetOffers
        //result = storeResource.iapGetOffers(new IAPOfferGetRequest(packageName: packageName, locale: new LocaleId('en_US'), country: new CountryId('US'), currency:  new CurrencyId('USD'))).get()
        //assert result.offers.items.size() == 1
        //OfferId offerId = result.offers.items[0].offerId
        OfferId offerId = testUtils.getByName('10_Birds')

        result = storeResource.preparePurchase(new PreparePurchaseRequest(userId: userId, offerId: offerId, country: new CountryId('US'), locale: new LocaleId('en_US'), currency: new CurrencyId('USD'),
                iapParams: new IAPParams(packageName: packageName, packageVersion: '1.0', packageSignatureHash: 'abc'))).get()
        assert result.purchaseToken != null

        storeResource.selectInstrumentForPurchase(new SelectInstrumentRequest(purchaseToken: result.purchaseToken, userId: userId, instrumentId: billingProfile.instruments[0].instrumentId)).get()
        result = storeResource.commitPurchase(new CommitPurchaseRequest(userId: userId, purchaseToken: result.purchaseToken)).get()
        assert result.entitlements.size() == 1
        assert result.entitlements[0].iapEntitlementData != null
        assert result.entitlements[0].iapSignature != null
        assert result.entitlements[0].signatureTimestamp != null

        result = storeResource.iapConsumeEntitlement(new IAPEntitlementConsumeRequest(
                userId: userId,
                entitlementId: result.entitlements[0].entitlementId,
                useCountConsumed: result.entitlements[0].useCount,
                trackingGuid: UUID.randomUUID().toString(),
                packageName: result.entitlements[0].packageName
        )).get()
        assert result.consumption.signatureTimestamp != null
        assert result.consumption.iapConsumptionData != null
    }

}
