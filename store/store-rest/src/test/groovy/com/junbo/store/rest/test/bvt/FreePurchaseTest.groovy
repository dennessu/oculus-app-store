package com.junbo.store.rest.test.bvt

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.OfferId
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions
import com.junbo.store.rest.test.Generator
import com.junbo.store.spec.model.PageParam
import com.junbo.store.spec.model.purchase.MakeFreePurchaseRequest
import org.testng.annotations.Test

/**
 * The FreePurchaseTest class.
 */
class FreePurchaseTest extends TestBase {

    @Test
    public void testFreePurchase() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.token = result.accessToken
        testUtils.verifyUserEmail(result.userId)
        freeOfferNames.each { String offerName ->
            OfferId offerId = testUtils.getByName(offerName)
            result = storeResource.makeFreePurchase(new MakeFreePurchaseRequest(offer: offerId, country: new CountryId('US'))).get()
            assert result.entitlements.size() == 1
            assert result.order != null

            def entitlement = result.entitlements[0]
            assert entitlement.entitlementType == 'DOWNLOAD'
            assert entitlement.itemType == 'APP'
            assert entitlement.item != null

            assert storeResource.getEntitlements(new PageParam()).get().entitlements.size() != 0

            result = downloadUrlResource.getDownloadUrl(entitlement.item, new DownloadUrlGetOptions(entitlementId: entitlement.self, platform: 'ANDROID')).get()
            assert result.redirectUrl != null
        }
    }


    @Test
    public void testFreePurchaseInvalidOffer() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.token = result.accessToken
        testUtils.verifyUserEmail(result.userId)

        try {
            storeResource.makeFreePurchase(new MakeFreePurchaseRequest(offer: testUtils.getByName(digitalOfferName), country: new CountryId('US'))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.110'
        }

        try {
            storeResource.makeFreePurchase(new MakeFreePurchaseRequest(offer: testUtils.getByName(physicalOfferName), country: new CountryId('US'))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.110'
        }
    }


    @Test
    public void testFreePurchaseOfferNotFound() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testUtils.verifyUserEmail(result.userId)
        testAccessTokenProvider.token = result.accessToken

        try {
            storeResource.makeFreePurchase(new MakeFreePurchaseRequest(offer: new OfferId('abcde'), country: new CountryId('US'))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '123.004'
        }
    }

    @Test
    public void testFreePurchaseEmailNotVerified() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.token = result.accessToken

        try {
            OfferId offerId = testUtils.getByName(freeOfferNames[0])
            storeResource.makeFreePurchase(new MakeFreePurchaseRequest(offer: offerId, country: new CountryId('US'))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.112'
        }
    }
}
