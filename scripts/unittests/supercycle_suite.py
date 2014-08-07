#!/usr/bin/python
from silkcloudut import *
import ut_oauth
import ut_checkout

if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(ut_checkout.CheckoutTests('testCheckout'))
    suite.addTest(ut_checkout.CheckoutTests('testCatalogGetAll'))
    suite.addTest(ut_oauth.OAuthTests('testSilentLogin'))
    suite.addTest(ut_oauth.OAuthTests('testRegisterImplicitFlow'))
    suite.addTest(ut_oauth.OAuthTests('testSilentLoginImplicitFlow'))
    suite.addTest(ut_oauth.OAuthTests('testResetPassword'))
    suite.addTest(ut_oauth.OAuthTests('testGetCountries'))
    # TODO: add testTFA

    silkcloud_utmain(suite)
