#!/usr/bin/python
from silkcloudut import *
import ut_health
import ut_oauth
import ut_checkout
import ut_checkout_consumable
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
    suite.addTest(ut_checkout_consumable.CheckoutTests('testCheckout'))
    # TODO: add testTFA

    silkcloud_utmain(suite)
