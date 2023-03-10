#!/usr/bin/python
from silkcloudut import *
import ut_health
import ut_oauth
import ut_checkout
import ut_checkout_consumable
import ut_checkout_error
import ut_identity

if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(ut_health.HealthTests('testHealth'))
    suite.addTest(ut_checkout.CheckoutTests('testCheckout'))
    suite.addTest(ut_checkout.CheckoutTests('testCatalogGetAll'))
    suite.addTest(ut_oauth.OAuthTests('testSilentLogin'))
    suite.addTest(ut_oauth.OAuthTests('testRegisterWithTosChallenge'))
    suite.addTest(ut_oauth.OAuthTests('testSilentLoginWithTosChallenge'))
    suite.addTest(ut_oauth.OAuthTests('testLoginWithTosChallenge'))
    suite.addTest(ut_oauth.OAuthTests('testRegisterImplicitFlow'))
    suite.addTest(ut_oauth.OAuthTests('testSilentLoginImplicitFlow'))
    suite.addTest(ut_oauth.OAuthTests('testLogout'))
    suite.addTest(ut_oauth.OAuthTests('testWildcardLogout'))
    suite.addTest(ut_oauth.OAuthTests('testResetPassword'))
    suite.addTest(ut_oauth.OAuthTests('testGetCountries'))
    suite.addTest(ut_identity.IdentityTests('testGroupMembershipDeletion'))
    suite.addTest(ut_identity.IdentityTests('testRoles'))
    suite.addTest(ut_identity.IdentityTests('testUserAttribute'))
    suite.addTest(ut_identity.IdentityTests('testGetAllUsers'))
    suite.addTest(ut_checkout_consumable.CheckoutTests('testCheckout'))
    suite.addTest(ut_checkout.CheckoutTests('testFullRefund'))
    suite.addTest(ut_checkout.CheckoutTests('testRefundTax'))
    suite.addTest(ut_checkout_error.CheckoutTests('testCheckoutError'))

    silkcloud_utmain(suite)
