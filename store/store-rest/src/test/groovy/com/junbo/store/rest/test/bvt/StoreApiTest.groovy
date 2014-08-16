package com.junbo.store.rest.test.bvt
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OfferId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.store.rest.test.Generator
import com.junbo.store.rest.test.TestAccessTokenProvider
import com.junbo.store.rest.test.TestUtils
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.billing.BillingProfileGetRequest
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest
import com.junbo.store.spec.model.identity.PersonalInfo
import com.junbo.store.spec.model.identity.StoreUserProfile
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.login.UserCredential
import com.junbo.store.spec.model.login.UserSignInRequest
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.LoginResource
import com.junbo.store.spec.resource.proxy.StoreResourceClientProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
/**
 * The StoreApiTest class.
 */
@Test
class StoreApiTest extends TestBase {

    public static String packageName = 'com.oculusvr.store.iap.sample'

    public static LocaleId locale = null // new LocaleId('en_US')

    @Test
    public void testUserProfile() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()
        String headline = Generator.genHeadline()
        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.setToken(result.accessToken)
        def userId = result.userId

        result = storeResource.getUserProfile().get()
        assert result.userProfile.userId == userId
        assert result.userProfile.email.value == email
        assert !result.userProfile.email.isValidated

        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(headline : headline))).get()
        assert result.userProfile.headline == headline
        assert result.userProfile.userId == userId
        assert result.userProfile.email.value == email

        def newPassword = Generator.genPassword()
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword))).get()
        assert result.challenge.type == 'PASSWORD'

        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword),
            challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: password))).get()

        result = loginResource.signIn(new UserSignInRequest(username: username, userCredential: new UserCredential(type: 'PASSWORD', value: newPassword))).get()
        assert result.accessToken != null
        /*
        ADD_PHONE(false),
        REMOVE_PHONE(true),
        UPDATE_DEFAULT_PHONE(true),
        UPDATE_NAME(false);*/
    }

    @Test
    public void testBillingProfile() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.setToken(result.accessToken)
        def userId = result.userId

        Instrument instrument = Generator.generateCreditCardInstrument()

        result = storeResource.getBillingProfile(new BillingProfileGetRequest(locale: locale, country: new CountryId('US'))).get()
        def billingProfile = storeResource.updateInstrument(new InstrumentUpdateRequest(
                locale: locale, country: new CountryId('US'),
                instrument: instrument),
        ).get().getBillingProfile()
        assert billingProfile.instruments.size() == 1
        assert billingProfile.instruments[0].isDefault
        def defaultPI = billingProfile.instruments[0].self

        billingProfile = storeResource.updateInstrument(new InstrumentUpdateRequest(
                locale: locale, country: new CountryId('US'),
                instrument: instrument)).get().billingProfile
        def newPI = billingProfile.instruments.find {Instrument pi -> pi.self != defaultPI}
        assert billingProfile.instruments.size() == 2
        assert billingProfile.instruments.find {Instrument pi -> pi.self == defaultPI}.isDefault
        assert !newPI.isDefault

        newPI.isDefault = true
        billingProfile = storeResource.updateInstrument(new InstrumentUpdateRequest(instrument: newPI, locale: locale, country: new CountryId('US'))).get().billingProfile
        assert !billingProfile.instruments.find {Instrument pi -> pi.self != newPI.self}.isDefault
        assert billingProfile.instruments.find {Instrument pi -> pi.self == newPI.self}.isDefault
    }

    @Test
    public void testAppPurchase() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.setToken(result.accessToken)
        def userId = result.userId

        Instrument instrument = Generator.generateCreditCardInstrument()

        result = storeResource.getBillingProfile(new BillingProfileGetRequest(locale: locale, country: new CountryId('US'))).get()
        def billingProfile = storeResource.updateInstrument(new InstrumentUpdateRequest(
                locale: locale, country: new CountryId('US'),
                instrument: instrument),
                ).get().getBillingProfile()

        assert billingProfile.instruments.size() == 1

        OfferId offerId = testUtils.getByName('testOffer_CartCheckout_Digital1')
        result = storeResource.preparePurchase(new PreparePurchaseRequest(offer: offerId, country: new CountryId('US'), locale: new LocaleId('en_US'))).get()
        assert result.purchaseToken != null
        assert result.challenge.type == 'PIN'

        try {
            result = storeResource.preparePurchase(new PreparePurchaseRequest(offer: offerId, country: new CountryId('US'), locale: locale, purchaseToken: result.purchaseToken,
                    challengeAnswer: new ChallengeAnswer(type: 'PIN', pin: pin + '1'))).get()
        } catch (AppErrorException ex) {
            assert ex != null
        }

        result = storeResource.preparePurchase(new PreparePurchaseRequest(offer: offerId, country: new CountryId('US'), locale: locale, purchaseToken: result.purchaseToken,
                challengeAnswer: new ChallengeAnswer(type: 'PIN', pin: pin))).get()
        assert result.challenge == null
        assert result.instrument.self == billingProfile.instruments[0].self

        result = storeResource.preparePurchase(new PreparePurchaseRequest(offer: offerId, country: new CountryId('US'), locale: locale, purchaseToken: result.purchaseToken,
                challengeAnswer: new ChallengeAnswer(type: 'PIN', pin: pin), instrument: billingProfile.instruments[0].self)).get()
        assert result.challenge == null

        result = storeResource.commitPurchase(new CommitPurchaseRequest(purchaseToken: result.purchaseToken)).get()
        assert result.entitlements.size() == 1
        assert result.order != null

        assert result.entitlements[0].entitlementType == 'DOWNLOAD'
        assert result.entitlements[0].itemType == 'APP'
        assert result.entitlements[0].item != null
    }

    @Test(enabled = false)
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
                userId: userId, locale: locale,
                action: BillingProfileUpdateRequest.UpdateAction.ADD_PI.name(), instrument: instrument),
        ).get().getBillingProfile()

        assert billingProfile.instruments.size() == 1

        // todo use iapGetOffers
        //result = storeResource.iapGetOffers(new IAPOfferGetRequest(packageName: packageName, locale: new LocaleId('en_US'), country: new CountryId('US'), currency:  new CurrencyId('USD'))).get()
        //assert result.offers.items.size() == 1
        //OfferId offerId = result.offers.items[0].offerId
        OfferId offerId = testUtils.getByName('10_Birds')

        result = storeResource.preparePurchase(new PreparePurchaseRequest(userId: userId, offerId: offerId, country: new CountryId('US'), locale: locale, currency: new CurrencyId('USD'),
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
