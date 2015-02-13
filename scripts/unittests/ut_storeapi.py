#!/usr/bin/python
import silkcloudut as ut
import ut_oauth
from silkcloudut import *

oauth = ut_oauth.OAuthTests('testRegister')
class StoreTests(ut.TestBase):
    def testRegister(self, isFBUser = False):
        ut.cookies.clear()

        storeApiHeaders = self.getStoreApiHeaders();

        tos = curlJson('GET', ut.test_uri, '/v1/horizon-api/id/tos', headers = storeApiHeaders)

        # generate user info
        user = oauth.genUserInfo(isFBUser)

        registerResp = curlJson('POST', ut.test_uri, '/v1/horizon-api/id/register', headers = storeApiHeaders, data = {
            "challengeAnswer" : None,
            "username" : user.username,
            "firstName" : user.first_name,
            "middleName" : None,
            "lastName" : user.last_name,
            "nickName" : user.nickname,
            "password" : user.password,
            "pin" : user.pin,
            "cor" : "US",
            "preferredLocale" : "en_US",
            "email" : user.email,
            "dob" : user.dob,
            "tosAgreed" : tos['self'],
            "newsPromotionsAgreed" : True
        })

        user.id = registerResp['user']['id']
        user.href = registerResp['user']['href']
        user.access_token = registerResp['accessToken']
        user.refresh_token = registerResp['refreshToken']

        # verify email
        testToken = curlForm('POST', ut.test_uri, '/v1/oauth2/token', data = {
            "client_id": ut.test_client_id,
            "client_secret": ut.test_client_secret,
            "scope": "smoketest identity catalog",
            "grant_type": "client_credentials"
        })

        emailVerifyLink = curlJson('GET', ut.test_uri, '/v1/oauth2/verify-email/test',
                                   headers = self.getStoreApiHeaders(testToken['access_token']),
                                   query = {
            "userId": user.id,
            "locale": "en_US",
            "email": user.email
        })[0]

        evc = getqueryparam(emailVerifyLink, 'evc')

        verifyEmailResp = curlJson('POST', ut.test_uri, '/v1/horizon-api/id/confirm-email', data = {
            'evc' : evc
        }, headers = self.getStoreApiHeaders(user.access_token))

        assert verifyEmailResp['isSuccess']
        assert verifyEmailResp['email'] == user.email

        return user

    def testLogin(self):
        user = self.testRegister()
        storeApiHeaders = self.getStoreApiHeaders();

        loginResp = curlJson('POST', ut.test_uri, '/v1/horizon-api/id/log-in', headers = storeApiHeaders, data = {
            "challengeAnswer" : None,
            "email" : user.email,
            "userCredential": {
                "type": "password",
                "value": user.password
            }
        })

        user.id = loginResp['user']['id']
        user.href = loginResp['user']['href']
        user.access_token = loginResp['accessToken']
        user.refresh_token = loginResp['refreshToken']

        return user


    def getStoreApiHeaders(self, token = None):
        headers = {
            "X-ANDROID-ID": randomstr(16, string.digits + char_range('A', 'F')),
            "Accept-Language": "en-US",
            "User-Agent": "Python UT"
        }
        if token:
            headers["Authorization"] = "Bearer " + token
        return headers

if __name__ == '__main__':
    silkcloud_utmain()
